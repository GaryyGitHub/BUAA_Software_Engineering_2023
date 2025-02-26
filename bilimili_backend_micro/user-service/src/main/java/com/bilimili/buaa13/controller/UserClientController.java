package com.bilimili.buaa13.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bilimili.buaa13.entity.*;
import com.bilimili.buaa13.entity.dto.UserDTO;
import com.bilimili.buaa13.im.IMServer;
import com.bilimili.buaa13.mapper.FavoriteMapper;
import com.bilimili.buaa13.mapper.FavoriteVideoMapper;
import com.bilimili.buaa13.mapper.UserMapper;
import com.bilimili.buaa13.service.favorite.FavoriteVideoService;
import com.bilimili.buaa13.service.message.MessageUnreadService;
import com.bilimili.buaa13.service.user.UserService;
import com.bilimili.buaa13.service.utils.CurrentUser;
import com.bilimili.buaa13.tools.RedisTool;
import io.netty.channel.Channel;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserClientController {

    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private UserService userService;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private FavoriteVideoMapper favoriteVideoMapper;
    @Autowired
    private RedisTool redisTool;
    @Autowired
    private IMServer imServer;
    @Autowired
    private MessageUnreadService messageUnreadService;
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private FavoriteVideoService favoriteVideoService;
    @Autowired
    private UserMapper userMapper;

    //从userService中寻找提供的服务
    @GetMapping("/user/{uid}")
    @SentinelResource(value = "getUserById",blockHandler = "getUserByIdHandler")
    public UserDTO getUserById(@PathVariable("uid") Integer uid){
        return userService.getUserByUId(uid);
    }
    public UserDTO getUserByIdHandler (@PathVariable("uid") Integer uid, BlockException exception){
        return new UserDTO();
    }

    @PostMapping("/user/currentUser/getId")
    @SentinelResource(value = "getCurrentUserId",blockHandler = "getCurrentUserIdHandler")
    public Integer getCurrentUserId(){
        return currentUser.getUserId();
    }
    public Integer getCurrentUserIdHandler(BlockException exception){return 1;}


    @PostMapping("/user/currentUser/isAdmin")
    @SentinelResource(value = "currentIsAdmin",blockHandler = "currentIsAdminHandler")
    public Boolean currentIsAdmin(){
        return currentUser.isAdmin();
    }
    public Boolean getCurrentIsAdminHandler(BlockException exception){return false;}

    @PostMapping("/user/updateFavoriteVideo")
    @SentinelResource(value = "updateFavoriteVideo", blockHandler = "updateFavoriteVideoHandler")
    public ResponseEntity<Void> updateFavoriteVideo(@RequestBody List<Map<String, Object>> result, @RequestParam("fid") Integer fid) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            result.stream().parallel().forEach(map -> {
                Video video = (Video) map.get("video");
                QueryWrapper<FavoriteVideo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("vid", video.getVid()).eq("fid", fid);
                map.put("info", favoriteVideoMapper.selectOne(queryWrapper));
            });
            sqlSession.commit();
        }
        return ResponseEntity.ok().build();
    }
    public ResponseEntity<Void> updateFavoriteVideoHandler(@RequestBody List<Map<String, Object>> result, @RequestParam("fid") Integer fid,BlockException exception) {
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/user/handle_comment")
    @SentinelResource(value = "handleComment",blockHandler = "handleCommentHandler")
    public void handleComment(@RequestParam("uid") Integer uid,
                              @RequestParam("toUid") Integer toUid,
                              @RequestParam("id") Integer id){

        if(!toUid.equals(uid)) {
            //1注释Redis
            redisTool.storeZSet("reply_zset:" + toUid, id);
            messageUnreadService.addOneUnread(toUid, "reply");

            // 通知未读消息
            Map<String, Object> map = new HashMap<>();
            map.put("type", "接收");
            Set<Channel> commentChannel = IMServer.userChannel.get(toUid);
            if (commentChannel != null) {
                commentChannel.stream().parallel().forEach(channel -> channel.writeAndFlush(IMResponse.message("reply", map)));
            }
        }
    }
    public void handleCommentHandler(@RequestParam("uid") Integer uid,
                                     @RequestParam("toUid") Integer toUid,
                                     @RequestParam("id") Integer id,
                                     BlockException exception){
        System.out.println("commentService fallback" + uid+" "+toUid+" "+id);
    }

    @PostMapping("/user/set/favorite")
    @SentinelResource(value = "setFavorite",blockHandler = "setFavoriteHandler")
    public ResponseResult setFavorite(@RequestParam("fid") Integer fid ,
                               @RequestParam("vid") String vids){
        ResponseResult responseResult = new ResponseResult();
        List<Integer> videoList = new ArrayList<>();
        String[] videos = vids.split(",");
        for (String s : videos) {
            try {
                videoList.add(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                // 处理可能的转换异常
                System.err.println("Invalid number format: " + s);
            }
        }
        Integer uid = fid;
        QueryWrapper<Favorite> favoriteQueryWrapper = new QueryWrapper<>();
        favoriteQueryWrapper.eq("fid", uid).eq("type", 1);
        Favorite favorite = favoriteMapper.selectOne(favoriteQueryWrapper);
        if(favorite == null){
            responseResult.setCode(404);
            responseResult.setMessage("Favorite not found");
            return responseResult;
        }
        Set<Integer>addSet = new HashSet<>();
        List<Integer> collectedVid = favoriteVideoMapper.getVidByFid(fid);
        addSet.add(fid);
        int flag = 0;
        for(Integer vid:videoList){
            if(collectedVid.contains(vid)){
                flag++;
            }
            else favoriteVideoService.addToFav(uid,vid,addSet);
        }
        if(flag >= videoList.size()){
            responseResult.setData(true);
        }
        else {
            responseResult.setData(false);
        }
        return responseResult;
    }
    public ResponseResult setFavoriteHandler(@RequestParam("fid") Integer fid ,
                                             @RequestParam("vid") String vids,
                                             BlockException exception){
        return new ResponseResult(404,"favorite fallback", null);
    }

    @GetMapping("/user/getUserByName/{account}")
    User getUserByName(@PathVariable("account") String account){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        queryWrapper.ne("state", 2);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            System.out.println("found user by name failed");
            return null;
        }

        return user;
    }

    @GetMapping("/user/getVidsByFid/{fid}")
    List<Integer> getVidsByFid(@PathVariable("fid") Integer fid){
        return favoriteVideoMapper.getVidByFid(fid);
    }

    @GetMapping("/user/getTimeByFid/{fid}")
    List<Date> getTimeByFid(@PathVariable("fid") Integer fid){
        return favoriteVideoMapper.getTimeByFid(fid);
    }
}
