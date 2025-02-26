package com.bilimili.buaa13.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.bilimili.buaa13.entity.ResponseResult;
import com.bilimili.buaa13.entity.Video;
import com.bilimili.buaa13.entity.VideoStatus;
import com.bilimili.buaa13.mapper.VideoMapper;
import com.bilimili.buaa13.service.video.VideoStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

/**
 * 来自video-service模块，向user-service提供被需求服务
 *
 *
 * **/
@Service
@RestController
@RequestMapping("/video")
public class ClientController {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private VideoStatusService videoStatusService;


    // 通过视频ID获取视频详情
    @GetMapping("/{vid}")
    @SentinelResource(value = "getVideoById",blockHandler = "getVideoByIdHandler")
    public ResponseResult getVideoById(@PathVariable("vid") Integer vid) {
        Video video = videoMapper.selectById(vid);
        if (video != null) {
            return new ResponseResult(200, "OK", video);
        } else {
            return new ResponseResult(404, "Video not found", null);
        }
    }
    public ResponseResult getVideoByIdHandler(@PathVariable("vid") Integer vid, BlockException exception) {
        return new ResponseResult(404, "Video not found fallback", null);
    }

    // 通过视频ID获取视频状态
    @GetMapping("/videoStatus/{vid}")
    @SentinelResource(value = "getVideoStatusById",blockHandler = "getVideoStatusByIdHandler")
    public VideoStatus getVideoStatusById(@PathVariable("vid") Integer vid) {
        return videoStatusService.getStatusByVideoId(vid);
    }
    public ResponseResult getVideoStatusByIdHandler(@PathVariable("vid") Integer vid, BlockException exception) {
        return new ResponseResult(404, "VideoStatus not found fallback", null);
    }

    // 更新视频状态
    @PostMapping("/updateStatus")
    @SentinelResource(value = "updateVideoStatus",blockHandler = "updateVideoStatusHandler")
    public ResponseResult updateVideoStatus(@RequestParam("vid") Integer vid,
                                            @RequestParam("statusType") String statusType,
                                            @RequestParam("increment") Boolean increment,
                                            @RequestParam("count") Integer count) {

        try {
            videoStatusService.updateVideoStatus(vid, statusType, increment, count);
            return new ResponseResult(200, "update video status success", null);
        } catch (Exception e) {
            return new ResponseResult(500, "update video status failed", null);
        }
    }

    public ResponseResult updateVideoStatusHandler(@PathVariable("vid") Integer vid,
                                                   @RequestParam("statusType") String statusType,
                                                   @RequestParam("increment") Boolean increment,
                                                   @RequestParam("count") Integer count,
                                                   BlockException exception) {
        return new ResponseResult(404, "update VideoStatus not found fallback", null);
    }

    // 更新视频的点赞或差评数
    @PostMapping("/updateGoodAndBad")
    @SentinelResource(value = "updateGoodAndBad",blockHandler = "updateGoodAndBadHandler")
    public ResponseResult updateGoodAndBad(@RequestParam("vid") Integer vid,
                                           @RequestParam("addGood") Boolean addGood) {
        try {
            videoStatusService.updateGoodAndBad(vid, addGood);
            return new ResponseResult(200, "update video status success", null);
        } catch (Exception e) {
            return new ResponseResult(500, "update video status failed", null);
        }
    }
    public ResponseResult updateGoodAndBadHandler(@RequestParam("vid") Integer vid,
                                                  @RequestParam("addGood") Boolean addGood,
                                                  BlockException exception) {
        return new ResponseResult(404, "update GoodBad not found fallback", null);
    }

    @GetMapping("/provider/sentinel/test/{message}")
    @SentinelResource(value = "providerSentinelTest", blockHandler = "handlerBlockHandler")
    public String providerSentinelTest(@PathVariable("message") String message) {
        return "sentinel测试：" + message;
    }

    public String handlerBlockHandler(@PathVariable("message") String message, BlockException exception) {
        return "providerSentinelTest服务不可用，" + "触发sentinel流控配置规则"+"\t"+"o(╥﹏╥)o";
    }
}
