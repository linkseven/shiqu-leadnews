package com.shiqu.behavior.service;

import com.shiqu.model.article.vos.HotArticleVo;

import java.util.List;

public interface HotArticleService {
    List<HotArticleVo> getArticleBySimilarity();
}