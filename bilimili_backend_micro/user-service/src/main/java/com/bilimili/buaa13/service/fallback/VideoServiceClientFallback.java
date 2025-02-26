package com.bilimili.buaa13.service.fallback;

import com.bilimili.buaa13.entity.ResponseResult;
import com.bilimili.buaa13.entity.Video;
import com.bilimili.buaa13.entity.VideoStatus;
import com.bilimili.buaa13.service.client.VideoServiceClient;
import org.springframework.stereotype.Component;

@Component
public class VideoServiceClientFallback implements VideoServiceClient {
    @Override
    public Video getVideoById(Integer vid) {
        return null;
    }

    @Override
    public VideoStatus getVideoStatusById(Integer vid) {
        return null;
    }

    @Override
    public ResponseResult updateVideoStatus(Integer vid, String statusType, Boolean increment, Integer count) {
        return null;
    }

    @Override
    public ResponseResult updateGoodAndBad(Integer vid, Boolean addGood) {
        return null;
    }
    @Override
    public String getProviderTest(String message) {
        return "对方服务不可用，开始服务降级处理";
    }

    @Override
    public String providerSentinelTest(String message) {
        return "对方服务不可用，开始服务降级处理";
    }
}
