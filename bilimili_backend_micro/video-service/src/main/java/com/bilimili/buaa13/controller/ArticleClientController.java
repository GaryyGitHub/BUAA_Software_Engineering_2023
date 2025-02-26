package com.bilimili.buaa13.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bilimili.buaa13.entity.Video;
import com.bilimili.buaa13.mapper.VideoMapper;
import com.bilimili.buaa13.mapper.VideoStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ArticleClientController {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private VideoStatusMapper videoStatusMapper;
    @GetMapping("/video/get")
    public Video getVideo(@RequestParam("vid") Integer vid){
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("vid", vid).ne("status", 3);
        //Viode = articleMapper.selectOne(queryWrapper);
        return videoMapper.selectOne(queryWrapper);
    }

    @PostMapping("/video/set/history")
    public void setHistory(@RequestParam List<Integer> vidList,
                           @RequestParam Map<String,Object> dataMap){
        HistoryController.setHistoryMap(vidList, videoMapper, videoStatusMapper,dataMap);
    }
}
