package com.shiqu.apis.article.fallback;

import com.shiqu.apis.article.IArticleClient;
import com.shiqu.model.article.dtos.ArticleDto;
import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

@Component
public class IArticleClientFallback implements IArticleClient {
    @Override
    public ResponseResult saveArticle(ArticleDto dto)  {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"获取数据失败");
    }
}