package com.bilimili.buaa13.controller;

import com.bilimili.buaa13.entity.ResponseResult;
import com.bilimili.buaa13.service.message.ChatService;
import com.bilimili.buaa13.service.utils.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class ChatController {
    @Autowired
    private ChatService chatService;

    @Autowired
    private CurrentUser currentUser;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 新建一个聊天，与其他用户首次聊天时调用
     * @param uid  对方用户ID
     * @return  响应对象 message可能值："新创建"/"已存在"/"未知用户"
     */
    @GetMapping("/msg/chat/create/{uid}")
    public ResponseResult createOneChat(@PathVariable("uid") Integer uid) {
       ResponseResult responseResult = new ResponseResult();
       //获取Chat,chat的细节
       Map<String, Object> result = chatService.createOneChat(uid, currentUser.getUserId());
       if (Objects.equals(result.get("msg").toString(), "新创建")) {//第一次聊天或之前的聊天被删除
           responseResult.setData(result);  // 返回新创建的聊天
       } else if (Objects.equals(result.get("msg").toString(), "未知用户")) {
           responseResult.setCode(404);
       }
       responseResult.setMessage(result.get("msg").toString());
       return responseResult;
    }

    /**
     * 获取用户最近的聊天列表
     * @param offset    分页偏移量（前端查询了多少个聊天）
     * @return  响应对象 包含带用户信息和最近一条消息的聊天列表以及是否还有更多数据
     */
    @GetMapping("/msg/chat/recent-list")
    public ResponseResult getRecentChatList(@RequestParam("offset") Long offset) {
        Integer uid = currentUser.getUserId();
        ResponseResult responseResult = new ResponseResult();
        Map<String, Object> map = new HashMap<>();
        map.put("list", chatService.getChatDataList(uid, offset));
        Long chat_num = redisTemplate.opsForZSet().zCard("chat_zset:" + uid);
        if(chat_num == null){return responseResult;}
        // 检查是否还有更多
        if (offset + 10 < chat_num) {
            map.put("more", true);
        } else {
            map.put("more", false);
        }
        responseResult.setData(map);
        return responseResult;
    }

    /**
     * 移除聊天
     * @param uid  对方用户ID
     * @return  响应对象
     */
    @GetMapping("/msg/chat/delete/{uid}")
    public ResponseResult deleteOneChat(@PathVariable("uid") Integer uid) {
        ResponseResult responseResult = new ResponseResult();
        chatService.deleteOneChat(uid, currentUser.getUserId());
        return responseResult;
    }

    /**
     * 切换窗口时 更新在线状态以及清除未读
     * @param from  对方UID
     */
    @GetMapping("/msg/chat/online")
    public void updateStateOnline(@RequestParam("from") Integer from) {
        Integer uid = currentUser.getUserId();
        chatService.updateStateOnline(from, uid);
    }

    /**
     * 切换窗口时 更新为离开状态 （该接口要放开，无需验证token，防止token过期导致用户一直在线）
     * @param from  对方UID
     */
    @GetMapping("/msg/chat/outline")
    public void updateStateOutline(@RequestParam("from") Integer from, @RequestParam("to") Integer to) {
        chatService.updateStateOutline(from, to);
    }
}
