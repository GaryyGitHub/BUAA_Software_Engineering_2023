package com.bilimili.buaa13.controller;

import com.bilimili.buaa13.entity.ResponseResult;
import com.bilimili.buaa13.entity.UserVideo;
import com.bilimili.buaa13.service.client.VideoServiceClient;
import com.bilimili.buaa13.service.favorite.FavoriteVideoService;
import com.bilimili.buaa13.service.favorite.FavoriteService;
import com.bilimili.buaa13.service.user.UserVideoService;
import com.bilimili.buaa13.service.utils.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class UserVideoController {

    @Autowired
    private VideoServiceClient videoServiceClient;



    @Autowired
    private CurrentUser currentUser;

    @Autowired
    private UserVideoService  userVideoService;

    @Autowired
    private FavoriteVideoService favoriteVideoService;



    /**
     * 登录用户播放视频时更新播放次数，有30秒更新间隔（防止用户刷播放量）
     * @param vid   视频ID
     * @return  返回用户与该视频的交互数据
     */
    /*
    @PostMapping("/video/play/user")
    public ResponseResult newPlayWithLoginUser(@RequestParam("vid") Integer vid) {
        Integer uid = currentUser.getUserId();
        ResponseResult customResponse = new ResponseResult();
        customResponse.setData(userVideoService.updatePlay(uid, vid));
        return customResponse;
    }*/

    /**
     * 登录用户播放视频时更新播放次数，有30秒更新间隔（防止用户刷播放量）
     * @param vid   视频ID
     * @return  返回用户与该视频的交互数据
     */
    @PostMapping("/video/play/user")
    public ResponseResult newPlayWithLoginUser(@RequestParam("vid") Integer vid) {
        Integer uid = currentUser.getUserId();
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(userVideoService.updatePlay(uid, vid));
        Set<Integer> fids = new HashSet<>();
        fids.add(uid+5000);
        favoriteVideoService.addToFav(uid,vid,fids);
        return responseResult;
    }

    /**
     * 点赞或点踩
     * @param vid   视频ID
     * @param isLove    赞还是踩 true赞 false踩
     * @param isSet     点还是取消 true点 false取消
     * @return 返回用户与该视频更新后的交互数据
     */
    @PostMapping("/user/video/love-or-not")
    public ResponseResult loveOrNot(@RequestParam("vid") Integer vid,
                                    @RequestParam("isLove") boolean isLove,
                                    @RequestParam("isSet") boolean isSet) {
        Integer uid = currentUser.getUserId();
        ResponseResult responseResult = new ResponseResult();
        responseResult.setData(userVideoService.setLoveOrUnlove(uid, vid, isLove, isSet));
        return responseResult;
    }

    /**
     * 接收来自 video-service 的更新用户视频播放记录的请求
     * @param userVideo 用户视频信息
     * @return 操作结果
     *
     *
     *
     *
     *
     * @PostMapping("/updateUserVideo")
     *     public ResponseEntity<String> updateUserVideo(@RequestBody UserVideo userVideo) {
     *         boolean success = userVideoService.updateUserVideo(userVideo);
     *         if (success) {
     *             return ResponseEntity.ok("User video data updated successfully.");
     *         } else {
     *             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user video data.");
     *         }
     *     }
     */


}
