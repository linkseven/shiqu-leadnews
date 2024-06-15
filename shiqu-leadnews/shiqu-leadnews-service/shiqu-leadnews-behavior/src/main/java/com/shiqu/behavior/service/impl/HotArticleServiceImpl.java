package com.shiqu.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shiqu.behavior.algorithm.Recommend;
import com.shiqu.behavior.mapper.ApUserArticleMapper;
import com.shiqu.behavior.service.HotArticleService;
import com.shiqu.behavior.constants.ArticleConstants;
import com.shiqu.common.redis.CacheService;
import com.shiqu.model.article.pojos.ApUserArticle;
import com.shiqu.model.article.vos.HotArticleVo;
import com.shiqu.model.article.vos.UserArticleStorageVo;
import com.shiqu.model.article.vos.UserArticleVo;
import com.shiqu.utils.thread.UserThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@Transactional
public class HotArticleServiceImpl implements HotArticleService {

    @Autowired
    private ApUserArticleMapper apUserArticleMapper;

    @Autowired
    private CacheService cacheService;

    /**
     * 通过相似度推荐
     */
    public List<HotArticleVo> getArticleBySimilarity(){
        //将apUserArticle表中数据取出并按一定规则赋值给userArticleStorageVos
        List<ApUserArticle> apUserArticles = apUserArticleMapper.selectList(new QueryWrapper<>());
        Set<Long> userIds = new HashSet<>();
        List<UserArticleStorageVo> userArticleStorageVos = new ArrayList<>();
        for (ApUserArticle apUserArticle : apUserArticles){
            userIds.add(apUserArticle.getUserId());
        }
        for (Long userId : userIds) {
            userArticleStorageVos.add(new UserArticleStorageVo(userId));
        }
        for (ApUserArticle apUserArticle : apUserArticles){
            List<UserArticleVo> userArticleVos = new ArrayList<>();
            for (UserArticleStorageVo userArticleStorageVo : userArticleStorageVos) {
                if (apUserArticle.getUserId().equals(userArticleStorageVo.getUserId())) {
                    UserArticleVo userArticleVo = new UserArticleVo();
                    userArticleVo.setChannelId(apUserArticle.getChannelId());
                    userArticleVo.setScore(apUserArticle.getScore());
                    userArticleVos.add(userArticleVo);
                }
            }
            int id = apUserArticle.getUserId().intValue();
            userArticleStorageVos.get(id-1).setUserArticleVos(userArticleVos);
        }

        //获取当前用户id，取得最高相似度用户的推荐文章
        Integer id = UserThreadUtil.getId();
        Recommend recommend = new Recommend();
        List<UserArticleVo> recommendationArticle = recommend.recommend(id, userArticleStorageVos);
        System.out.println(recommendationArticle);
        //综合结果并返回
        List<HotArticleVo> hotArticleVoList = new ArrayList<>();
        for (UserArticleVo userArticleVo : recommendationArticle){
            //取出频道id
            int channelId = userArticleVo.getChannelId();
            //从缓存中取出对应文章
            String jsonStr = cacheService.get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + channelId);
            List<HotArticleVo> channelArticle = null;
            if(StringUtils.isNotBlank(jsonStr)) {
                channelArticle = JSON.parseArray(jsonStr, HotArticleVo.class);
            }
            //将文章存入热文章列表中
            if (!Objects.requireNonNull(channelArticle).isEmpty()){
                hotArticleVoList.addAll(channelArticle);
            }
        }
        return hotArticleVoList;
    }
}