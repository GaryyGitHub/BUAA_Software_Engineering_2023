package com.bilimili.buaa13.service.client;

import com.bilimili.buaa13.config.FeignConfig;
import com.bilimili.buaa13.entity.Comment;
import com.bilimili.buaa13.entity.User;
import com.bilimili.buaa13.entity.UserVideo;
import com.bilimili.buaa13.entity.dto.UserDTO;
import com.bilimili.buaa13.service.fallback.UserServiceClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

//服务名称和url
//用于接收video微服务模块传输的对象
//自动将调用路由到'video-service'
@FeignClient(name = "user-service",configuration = FeignConfig.class,fallback = UserServiceClientFallback.class)
public interface UserServiceClient {

    //从userService中寻找提供的服务
    @GetMapping("/user/{uid}")
    UserDTO getUserById(@PathVariable("uid") Integer uid);


    @PostMapping("/user/currentUser/getId")
    Integer getCurrentUserId();

    @PostMapping("/user/currentUser/isAdmin")
    Boolean currentIsAdmin();

    @PostMapping("/user/updateFavoriteVideo")
    ResponseEntity<Void> updateFavoriteVideo(@RequestBody List<Map<String, Object>> result, @RequestParam("fid") Integer fid);

    @PostMapping("/user/handle_comment")
    void handleComment(@RequestParam("uid") Integer uid,
                       @RequestParam("toUid") Integer toUid,
                       @RequestParam("id") Integer id);

    //直接调用mapper中的方法即可
    @GetMapping("/user/getVidsByFid/{fid}")
    List<Integer> getVidsByFid(@PathVariable("fid") Integer fid);

    @GetMapping("/user/getTimeByFid/{fid}")
    List<Date> getTimeByFid(@PathVariable("fid") Integer fid);

    @GetMapping("/user/getUserByName/{Account}")
    User getUserByName(@PathVariable("Account") String account);

}
