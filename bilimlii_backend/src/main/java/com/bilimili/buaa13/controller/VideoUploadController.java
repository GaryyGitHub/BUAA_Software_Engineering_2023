package com.bilimili.buaa13.controller;

import com.bilimili.buaa13.entity.ResponseResult;
import com.bilimili.buaa13.entity.dto.VideoUploadInfoDTO;
import com.bilimili.buaa13.service.video.VideoUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class VideoUploadController {
    @Autowired
    private VideoUploadService videoUploadService;

    /**
     * 查询当前视频准备要上传的分片序号
     * @param hash 视频的hash值
     * @return
     */
    @GetMapping("/video/ask-chunk")
    public ResponseResult askChunk(@RequestParam("hash") String hash) {
        return videoUploadService.getNextCurrentFragment(hash);
    }

    /**
     * 上传分片
     * @param fragment 分片的blob文件
     * @param hash  视频的hash值
     * @param index 当前分片的序号
     * @return
     * @throws IOException
     */
    @PostMapping("/video/upload-chunk")
    public ResponseResult uploadFragment(@RequestParam("chunk") MultipartFile fragment,
                                      @RequestParam("hash") String hash,
                                      @RequestParam("index") Integer index) throws IOException {
        try {
            System.out.println("进入分片上传");
            return videoUploadService.uploadFragment(fragment, hash, index);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResult(500, "分片上传失败", null);
        }

    }

    /**
     * 取消上传
     * @param hash 视频的hash值
     * @return
     */
    @GetMapping("/video/cancel-upload")
    public ResponseResult cancelUpload(@RequestParam("hash") String hash) {
        return videoUploadService.cancelUploadAndDelete(hash);
    }

    /**
     * 添加视频投稿
     *
     * @param cover       封面文件
     * @param hash        视频的hash值
     * @param title       投稿标题
     * @param type        视频类型 1自制 2转载
     * @param auth        作者声明 0不声明 1未经允许禁止转载
     * @param duration    视频总时长
     * @param mainClassId 主分区ID
     * @param subClassId  子分区ID
     * @param tags        标签
     * @param description 简介
     * @return 响应对象
     */
    @PostMapping("/video/add")
    public ResponseResult addVideo(@RequestParam("cover") MultipartFile cover,
                                   @RequestParam("hash") String hash,
                                   @RequestParam("title") String title,
                                   @RequestParam("type") Integer type,
                                   @RequestParam("auth") Integer auth,
                                   @RequestParam("duration") Double duration,
                                   @RequestParam("mcid") String mainClassId,
                                   @RequestParam("scid") String subClassId,
                                   @RequestParam("tags") String tags,
                                   @RequestParam("descr") String description) {
        VideoUploadInfoDTO videoUploadInfoDTO = new VideoUploadInfoDTO(null, hash, title, type, auth, duration, mainClassId, subClassId, tags, description, null);
        try {
            return videoUploadService.setVideoMessage(cover, videoUploadInfoDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResult(500, "封面上传失败", null);
        }
    }
}
