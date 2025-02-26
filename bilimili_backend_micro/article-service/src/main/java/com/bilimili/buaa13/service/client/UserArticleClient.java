package com.bilimili.buaa13.service.client;

import com.bilimili.buaa13.entity.ResponseResult;
import com.bilimili.buaa13.entity.User;
import com.bilimili.buaa13.entity.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "userService", url = "http://userService:9090")
public interface UserArticleClient {
    @GetMapping("/user/dto/{uid}")
    UserDTO getUserDTOById(@PathVariable("uid") Integer uid);

    @GetMapping("/user/{uid}")
    User getUserById(@PathVariable("uid") Integer uid);

    @PostMapping("/user/handle_comment")
    void handleComment(@RequestParam("uid") Integer uid,
                       @RequestParam("toUid") Integer toUid,
                       @RequestParam("id") Integer id);
    @PostMapping("/user/currentUser/getId")
    Integer getCurrentUserId();

    @PostMapping("/user/currentUser/isAdmin")
    Boolean currentIsAdmin();

    @PostMapping("/user/set/favorite")
    ResponseResult setFavorite(@RequestParam("fid") Integer fid,
                               @RequestParam("vid") String vids);
}
