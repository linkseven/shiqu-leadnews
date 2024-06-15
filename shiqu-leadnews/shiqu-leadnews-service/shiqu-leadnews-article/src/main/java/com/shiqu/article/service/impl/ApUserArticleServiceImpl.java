package com.shiqu.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiqu.article.mapper.ApUserArticleMapper;
import com.shiqu.article.service.ApUserArticleService;
import com.shiqu.model.article.pojos.ApUserArticle;
import com.shiqu.utils.thread.UserThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class ApUserArticleServiceImpl extends ServiceImpl<ApUserArticleMapper, ApUserArticle> implements ApUserArticleService {
    @Override
    public Integer getUserId() {
        return UserThreadUtil.getId();
    }
}
