package com.bilimili.buaa13.service.impl.article;

import com.bilimili.buaa13.entity.ResponseResult;
import com.bilimili.buaa13.entity.dto.ArticleUploadDTO;
import com.bilimili.buaa13.service.article.ArticleUploadService;
import com.bilimili.buaa13.service.client.UserArticleClient;
import com.bilimili.buaa13.tools.OssTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class ArticleUploadServiceImpl implements ArticleUploadService {

    @Autowired
    private UserArticleClient userArticleClient;

    @Autowired
    private OssTool ossTool;

    @Override
    public ResponseResult addArticle(ArticleUploadDTO articleUploadDTO) {
        Integer loginUserId = userArticleClient.getCurrentUserId();
        articleUploadDTO.setUid(loginUserId);
        try {
            String url = ossTool.uploadArticle(articleUploadDTO.getContent());
        } catch (IOException e) {
            log.error("合并视频写库时出错了");
            log.error(e.getMessage());
        }
        return new ResponseResult();
    }
}
