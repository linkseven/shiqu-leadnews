package com.shiqu.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiqu.article.constants.ArticleConstants;
import com.shiqu.article.mapper.ApArticleConfigMapper;
import com.shiqu.article.mapper.ApArticleContentMapper;
import com.shiqu.article.mapper.ApArticleMapper;
import com.shiqu.article.service.ApArticleService;
import com.shiqu.article.service.ArticleFreemarkerService;
import com.shiqu.common.redis.CacheService;
import com.shiqu.model.article.dtos.ArticleDto;
import com.shiqu.model.article.dtos.ArticleHomeDto;
import com.shiqu.model.article.pojos.ApArticle;
import com.shiqu.model.article.pojos.ApArticleConfig;
import com.shiqu.model.article.pojos.ApArticleContent;
import com.shiqu.model.article.vos.HotArticleVo;
import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.model.common.enums.AppHttpCodeEnum;
import com.shiqu.model.mess.ArticleVisitStreamMess;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Autowired
    private ApArticleMapper mapper;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    //@Lazy
    private ArticleFreemarkerService articleFreemarkerService;

    private final static short MAX_PAGE_SIZE = 50;
    /**
     * 加载文章列表
     * @param dto
     * @param type  1 加载更多  2 加载最新
     * @return
     */
    @Override
    public ResponseResult load(ArticleHomeDto dto, Short type) {
        //1.校验参数
        //分页条数校验
        Integer size = dto.getSize();
        if (size == null || size == 0){
            size = 10;
            dto.setSize(size);
        }
        //分页最大值不超过50
        size = Math.min(size, MAX_PAGE_SIZE);
        dto.setSize(size);

        //校验type参数
        if (type != null){
            if (!type.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !type.equals(ArticleConstants.LOADTYPE_LOAD_NEW)){
                type = ArticleConstants.LOADTYPE_LOAD_MORE;
            }
        }

        //频道校验
        if (StringUtils.isBlank(dto.getTag()))
            dto.setTag(ArticleConstants.DEFAULT_TAG);

        //时间校验
        if (dto.getMaxBehotTime() == null)
            dto.setMaxBehotTime(new Date());
        if (dto.getMinBehotTime() == null)
            dto.setMinBehotTime(new Date());
        //2. 查询
        List<ApArticle> apArticles = mapper.loadArticleList(dto, type);
        //返回结果
        return ResponseResult.okResult(apArticles);
    }

    @Autowired
    private CacheService cacheService;
    /**
     * 加载文章列表
     * @param dto
     * @param type      1 加载更多   2 加载最新
     * @param firstPage true  是首页  false 非首页
     * @return
     */
    @Override
    public ResponseResult load2(ArticleHomeDto dto, Short type, boolean firstPage) {
        if(firstPage){
            String jsonStr = cacheService.get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + dto.getTag());
            if(StringUtils.isNotBlank(jsonStr)){
                List<HotArticleVo> hotArticleVoList = JSON.parseArray(jsonStr, HotArticleVo.class);
                return ResponseResult.okResult(hotArticleVoList);
            }
        }
        return load(dto, type);
    }

    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        //1.检查参数
        if(dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto,apArticle);

        //2.判断是否存在id
        if(dto.getId() == null){
            //2.1 不存在id  保存  文章  文章配置  文章内容

            //保存文章
            save(apArticle);

            //保存配置
            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            apArticleConfigMapper.insert(apArticleConfig);

            //保存 文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);

        }else {
            //2.2 存在id   修改  文章  文章内容

            //修改  文章
            updateById(apArticle);

            //修改文章内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getId()));
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }
        //异步调用 生成静态文件上传到minio中
        articleFreemarkerService.buildArticleToMinIO(apArticle,dto.getContent());

        log.info("========" +ResponseResult.okResult(apArticle.getId()).getCode().toString());
        //3.结果返回  文章的id
        return ResponseResult.okResult(apArticle.getId());
    }

    /**
     * 更新文章的分值  同时更新缓存中的热点文章数据
     * @param mess
     */
    @Override
    public void updateScore(ArticleVisitStreamMess mess) {
        //1.更新文章的阅读、点赞、收藏、评论的数量
        ApArticle apArticle = updateArticle(mess);
        //2.计算文章的分值
        Integer score = computeScore(apArticle);
        score = score * 3;

        //3.替换当前文章对应频道的热点数据
        replaceDataToRedis(apArticle, score, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + apArticle.getChannelId());

        //4.替换推荐对应的热点数据
        replaceDataToRedis(apArticle, score, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);

    }

    /**
     * 替换数据并且存入到redis
     * @param apArticle
     * @param score
     * @param s
     */
    private void replaceDataToRedis(ApArticle apArticle, Integer score, String s) {
        String articleListStr = cacheService.get(s);
        if (StringUtils.isNotBlank(articleListStr)) {
            List<HotArticleVo> hotArticleVoList = JSON.parseArray(articleListStr, HotArticleVo.class);

            boolean flag = true;

            //如果缓存中存在该文章，只更新分值
            for (HotArticleVo hotArticleVo : hotArticleVoList) {
                if (hotArticleVo.getId().equals(apArticle.getId())) {
                    hotArticleVo.setScore(score);
                    flag = false;
                    break;
                }
            }

            //如果缓存中不存在，查询缓存中分值最小的一条数据，进行分值的比较，如果当前文章的分值大于缓存中的数据，就替换
            if (flag) {
                if (hotArticleVoList.size() >= 30) {
                    hotArticleVoList = hotArticleVoList.stream().sorted(Comparator.comparing(HotArticleVo::getScore).reversed()).collect(Collectors.toList());
                    HotArticleVo lastHot = hotArticleVoList.get(hotArticleVoList.size() - 1);
                    if (lastHot.getScore() < score) {
                        hotArticleVoList.remove(lastHot);
                        HotArticleVo hot = new HotArticleVo();
                        BeanUtils.copyProperties(apArticle, hot);
                        hot.setScore(score);
                        hotArticleVoList.add(hot);
                    }


                } else {
                    HotArticleVo hot = new HotArticleVo();
                    BeanUtils.copyProperties(apArticle, hot);
                    hot.setScore(score);
                    hotArticleVoList.add(hot);
                }
            }
            //缓存到redis
            hotArticleVoList = hotArticleVoList.stream().sorted(Comparator.comparing(HotArticleVo::getScore).reversed()).collect(Collectors.toList());
            cacheService.set(s, JSON.toJSONString(hotArticleVoList));

        }
    }

    /**
     * 更新文章行为数量
     * @param mess
     */
    private ApArticle updateArticle(ArticleVisitStreamMess mess) {
        ApArticle apArticle = getById(mess.getArticleId());
        apArticle.setCollection(apArticle.getCollection()==null?0:apArticle.getCollection()+mess.getCollect());
        apArticle.setComment(apArticle.getComment()==null?0:apArticle.getComment()+mess.getComment());
        apArticle.setLikes(apArticle.getLikes()==null?0:apArticle.getLikes()+mess.getLike());
        apArticle.setViews(apArticle.getViews()==null?0:apArticle.getViews()+mess.getView());
        updateById(apArticle);
        return apArticle;

    }

    /**
     * 计算文章的具体分值
     * @param apArticle
     * @return
     */
    private Integer computeScore(ApArticle apArticle) {
        Integer score = 0;
        if(apArticle.getLikes() != null){
            score += apArticle.getLikes() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if(apArticle.getViews() != null){
            score += apArticle.getViews();
        }
        if(apArticle.getComment() != null){
            score += apArticle.getComment() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if(apArticle.getCollection() != null){
            score += apArticle.getCollection() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }

        return score;
    }
}
