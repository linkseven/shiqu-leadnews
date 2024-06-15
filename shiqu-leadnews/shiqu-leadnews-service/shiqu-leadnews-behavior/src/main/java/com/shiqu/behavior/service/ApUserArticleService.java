package com.shiqu.behavior.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shiqu.model.article.dtos.UserArticleDto;
import com.shiqu.model.article.pojos.ApUserArticle;

public interface ApUserArticleService extends IService<ApUserArticle> {
    void saveOrUpdate(UserArticleDto dto, Long addScore);
}
