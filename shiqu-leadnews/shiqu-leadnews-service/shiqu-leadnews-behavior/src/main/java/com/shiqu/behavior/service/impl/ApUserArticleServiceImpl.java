package com.shiqu.behavior.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiqu.behavior.mapper.ApArticleMapper;
import com.shiqu.behavior.mapper.ApUserArticleMapper;
import com.shiqu.behavior.service.ApUserArticleService;
import com.shiqu.model.article.dtos.UserArticleDto;
import com.shiqu.model.article.pojos.ApArticle;
import com.shiqu.model.article.pojos.ApUserArticle;
import com.shiqu.utils.thread.UserThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class ApUserArticleServiceImpl extends ServiceImpl<ApUserArticleMapper, ApUserArticle> implements ApUserArticleService {

/*    @Autowired
    private ApUserArticleMapper apUserArticleMapper;*/

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Override
    public void saveOrUpdate(UserArticleDto dto, Long addScore) {
        try{
            Long articleId = dto.getArticleId();
            ApArticle apArticle = apArticleMapper.selectById(articleId);
            log.info(dto.getArticleId().toString());
            Integer channelId = apArticle.getChannelId();
            Integer userId = UserThreadUtil.getId();
            ApUserArticle apUserArticle = new ApUserArticle();
            apUserArticle.setChannelId(channelId);
            apUserArticle.setUserId(userId.longValue());
            //判断一个用户是否存在某一频道
            LambdaQueryWrapper<ApUserArticle> queryWrapper = new QueryWrapper<ApUserArticle>().lambda();
            queryWrapper.eq(ApUserArticle::getUserId, userId).eq(ApUserArticle::getChannelId, channelId);
            ApUserArticle userArticle = getOne(queryWrapper);
            //ApUserArticle userArticle = apUserArticleMapper.selectOne(queryWrapper);
            if (userArticle == null){
//            apUserArticleMapper.insert(apUserArticle);
                save(apUserArticle);
            } else {
                apUserArticle.setScore(userArticle.getScore() + addScore);
                //apUserArticleMapper.update(apUserArticle, queryWrapper);
                update(apUserArticle, queryWrapper);
            }
        }catch (Exception e){
            System.out.println("空");
        }
    }
}
