package com.shiqu.apis.article;

import com.shiqu.apis.article.fallback.IArticleClientFallback;
import com.shiqu.model.article.dtos.ArticleDto;
import com.shiqu.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(value = "leadnews-article", fallback = IArticleClientFallback.class)
public interface IArticleClient {

    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto) ;
}
