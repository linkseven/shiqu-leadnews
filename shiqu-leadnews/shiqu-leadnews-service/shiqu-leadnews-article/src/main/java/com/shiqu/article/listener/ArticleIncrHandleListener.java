package com.shiqu.article.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.shiqu.article.service.ApArticleService;
import com.shiqu.common.constants.HotArticleConstants;
import com.shiqu.model.mess.ArticleVisitStreamMess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ArticleIncrHandleListener {

    @Autowired
    private ApArticleService apArticleService;

    @KafkaListener(topics = HotArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC)
    public void onMessage(String mess){
        if(StringUtils.isNotBlank(mess)){
            ArticleVisitStreamMess articleVisitStreamMess = JSON.parseObject(mess, ArticleVisitStreamMess.class);
            apArticleService.updateScore(articleVisitStreamMess);
        }
    }
}