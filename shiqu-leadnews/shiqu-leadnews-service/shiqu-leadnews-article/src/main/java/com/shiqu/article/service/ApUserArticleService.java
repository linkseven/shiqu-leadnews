package com.shiqu.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shiqu.model.article.pojos.ApUserArticle;

public interface ApUserArticleService extends IService<ApUserArticle> {
    Integer getUserId();
}
