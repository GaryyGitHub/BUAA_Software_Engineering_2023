package com.bilimili.buaa13.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bilimili.buaa13.entity.ResponseResult;
import com.bilimili.buaa13.entity.Video;
import com.bilimili.buaa13.entity.VideoStatus;
import com.bilimili.buaa13.mapper.VideoStatusMapper;
import com.bilimili.buaa13.mapper.VideoMapper;
import com.bilimili.buaa13.service.client.UserServiceClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class HistoryController {

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private VideoStatusMapper videoStatusMapper;


    /**
     * 获取历史记录
     * @param uid   用户uid
     */
    @GetMapping("/record/video")
    public ResponseResult getRecordVideoByUid(@RequestParam("uid") Integer uid) {
        ResponseResult responseResult = new ResponseResult();
        int fid = 5000+uid;
        List<Integer> vids = userServiceClient.getVidsByFid(fid);
        List<Date> times = userServiceClient.getTimeByFid(fid);
        Map<String, Object> dataMap = new HashMap<>();
        setHistoryMap(vids, videoMapper, videoStatusMapper, dataMap);
        dataMap.put("time",times);
        responseResult.setData(dataMap);
        return responseResult;
    }

    static void setHistoryMap(List<Integer> vids, VideoMapper videoMapper, VideoStatusMapper videoStatusMapper, Map<String,Object> dataMap) {
        List<String> titles = new ArrayList<>();
        List<Double> videoTimes = new ArrayList<>();
        List<Integer> playCounts = new ArrayList<>();
        List<String> urls = new ArrayList<>();
        for(Integer vid: vids){
            QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
            videoQueryWrapper.eq("vid", vid);
            QueryWrapper<VideoStatus> videoStatsQueryWrapper = new QueryWrapper<>();
            videoStatsQueryWrapper.eq("vid", vid);
            Video video = videoMapper.selectOne(videoQueryWrapper);
            VideoStatus videoStatus = videoStatusMapper.selectOne(videoStatsQueryWrapper);
            titles.add(video.getTitle());
            videoTimes.add(video.getDuration());
            urls.add(video.getCoverUrl());
            playCounts.add(videoStatus.getPlay());
        }
        dataMap.put("vid",vids);
        dataMap.put("title",titles);
        dataMap.put("duration", videoTimes);
        dataMap.put("url",urls);
        dataMap.put("view",playCounts);
    }
}
