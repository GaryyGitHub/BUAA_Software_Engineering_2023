package com.bilimili.buaa13.service.client;

import com.bilimili.buaa13.entity.Video;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "videoService", url = "http://videoService:9090")
public interface VideoArticleClient {
    @GetMapping("/video/get")
    Video getVideo(@RequestParam("vid") Integer vid);
    @PostMapping("/set/history")
    public void setHistory(@RequestParam List<Integer> vidList,
                           @RequestParam Map<String,Object> dataMap);
}
