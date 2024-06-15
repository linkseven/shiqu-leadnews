package com.shiqu.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shiqu.apis.behavior.IBehaviorClient;
import com.shiqu.apis.wemedia.IWemediaClient;
import com.shiqu.article.algorithm.Recommend;
import com.shiqu.article.mapper.ApArticleMapper;
import com.shiqu.article.mapper.ApUserArticleMapper;
import com.shiqu.article.service.ApUserArticleService;
import com.shiqu.article.service.HotArticleService;
import com.shiqu.common.constants.ArticleConstants;
import com.shiqu.common.redis.CacheService;
import com.shiqu.model.article.pojos.ApArticle;
import com.shiqu.model.article.pojos.ApUserArticle;
import com.shiqu.model.article.vos.HotArticleVo;
import com.shiqu.model.article.vos.UserArticleStorageVo;
import com.shiqu.model.article.vos.UserArticleVo;
import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.model.wemedia.pojos.WmChannel;
import com.shiqu.utils.thread.UserThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class HotArticleServiceImpl implements HotArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApUserArticleMapper apUserArticleMapper;

    /**
     * 计算热点文章
     */
    @Override
    public void computeHotArticle() {


        //1.查询所有的文章数据
        //Date dateParam = DateTime.now().minusDays(50).toDate();
        //List<ApArticle> apArticleList = apArticleMapper.findArticleListByLast5days(dateParam);
        List<ApArticle> apArticleList = apArticleMapper.selectList(new QueryWrapper<>());

        //2.计算文章的分值
        List<HotArticleVo> hotArticleVoList = computeHotArticle(apArticleList);

        //3.为每个频道缓存30条分值较高的文章
        cacheTagToRedis(hotArticleVoList);

    }

    @Autowired
    private IWemediaClient wemediaClient;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private IBehaviorClient behaviorClient;

    /**
     * 为每个频道缓存30条分值较高的文章
     * @param hotArticleVoList
     */
    private void cacheTagToRedis(List<HotArticleVo> hotArticleVoList) {
        //每个频道缓存30条分值较高的文章
        ResponseResult responseResult = wemediaClient.getChannels();
        if(responseResult.getCode().equals(200)){
            String channelJson = JSON.toJSONString(responseResult.getData());
            List<WmChannel> wmChannels = JSON.parseArray(channelJson, WmChannel.class);
            //检索出每个频道的文章
            if(wmChannels != null && wmChannels.size() > 0){
                for (WmChannel wmChannel : wmChannels) {
                    List<HotArticleVo> hotArticleVos = hotArticleVoList.stream().filter(x -> wmChannel.getId().equals(x.getChannelId())).collect(Collectors.toList());
                    //给文章进行排序，取30条分值较高的文章存入redis  key：频道id   value：30条分值较高的文章
                    sortAndCache(hotArticleVos, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + wmChannel.getId());
                }
            }
        }

        //设置推荐数据
        //给文章进行排序，取30条分值较高的文章存入redis  key：频道id   value：30条分值较高的文章
        //上案作废，取30条相关相似度最高用户的推荐文章存入redis
        List<HotArticleVo> articleBySimilarity = getArticleBySimilarity();
        //sortAndCache(hotArticleVoList, ArticleConstants.HOT_ARTICLE_FIRST_PAGE+ArticleConstants.DEFAULT_TAG);
        defaultCache(articleBySimilarity, ArticleConstants.HOT_ARTICLE_FIRST_PAGE+ArticleConstants.DEFAULT_TAG);
    }

    /**
     * 排序并且缓存数据
     * @param hotArticleVos
     * @param key
     */
    private void sortAndCache(List<HotArticleVo> hotArticleVos, String key) {
        hotArticleVos = hotArticleVos.stream().sorted(Comparator.comparing(HotArticleVo::getScore).reversed()).collect(Collectors.toList());
        if (hotArticleVos.size() > 30) {
            hotArticleVos = hotArticleVos.subList(0, 30);
        }
        cacheService.set(key, JSON.toJSONString(hotArticleVos));
    }

    /**
     * 首页推荐排序
     * @param hotArticleVos
     */
    private void defaultCache(List<HotArticleVo> hotArticleVos, String key) {
/*        hotArticleVos = hotArticleVos.stream().sorted(Comparator.comparing(HotArticleVo::getScore).reversed()).collect(Collectors.toList());
        return hotArticleVos;*/
        cacheService.set(key, JSON.toJSONString(hotArticleVos));
    }

    /**
     * 计算文章分值
     * @param apArticleList
     * @return
     */
    private List<HotArticleVo> computeHotArticle(List<ApArticle> apArticleList) {

        List<HotArticleVo> hotArticleVoList = new ArrayList<>();

        if(apArticleList != null && apArticleList.size() > 0){
            for (ApArticle apArticle : apArticleList) {
                HotArticleVo hot = new HotArticleVo();
                BeanUtils.copyProperties(apArticle,hot);
                Integer score = computeScore(apArticle);
                hot.setScore(score);
                hotArticleVoList.add(hot);
            }
        }
        return hotArticleVoList;
    }

    /**
     * 计算文章的具体分值
     * @param apArticle
     * @return
     */
    private Integer computeScore(ApArticle apArticle) {
        Integer scere = 0;
        if(apArticle.getLikes() != null){
            scere += apArticle.getLikes() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if(apArticle.getViews() != null){
            scere += apArticle.getViews();
        }
        if(apArticle.getComment() != null){
            scere += apArticle.getComment() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if(apArticle.getCollection() != null){
            scere += apArticle.getCollection() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }

        return scere;
    }
    @Autowired
    private ApUserArticleService apUserArticleService;

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
        for (UserArticleStorageVo userArticleStorageVo : userArticleStorageVos){
            List<UserArticleVo> userArticleVos = new ArrayList<>();
            for (ApUserArticle apUserArticle : apUserArticles) {
                if (userArticleStorageVo.getUserId().equals(apUserArticle.getUserId())) {
                    UserArticleVo userArticleVo = new UserArticleVo();
                    userArticleVo.setChannelId(apUserArticle.getChannelId());
                    userArticleVo.setScore(apUserArticle.getScore());
                    userArticleVos.add(userArticleVo);
                }
            }
            int id = userArticleStorageVo.getUserId().intValue();
            userArticleStorageVos.get(id-1).setUserArticleVos(userArticleVos);
        }

        //获取当前用户id，取得最高相似度用户的推荐文章
        Integer id = UserThreadUtil.getId();
        Recommend recommend = new Recommend();
        List<UserArticleVo> recommendationArticle = new ArrayList<>();
        if (id == 4){
            for (UserArticleStorageVo userArticleStorageVo : userArticleStorageVos){
                if (userArticleStorageVo.getUserId() == 4){
                    recommendationArticle = userArticleStorageVo.getUserArticleVos();
                }
            }
        } else {
            recommendationArticle = recommend.recommend(id, userArticleStorageVos);
        }

        //System.out.println(recommendationArticle);
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