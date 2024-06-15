package com.shiqu.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shiqu.behavior.mapper.ApArticleMapper;
import com.shiqu.behavior.mapper.ApUserArticleMapper;
import com.shiqu.behavior.service.ApLikesBehaviorService;
import com.shiqu.behavior.service.ApUserArticleService;
import com.shiqu.common.constants.BehaviorConstants;
import com.shiqu.common.constants.HotArticleConstants;
import com.shiqu.common.redis.CacheService;
import com.shiqu.model.article.dtos.UserArticleDto;
import com.shiqu.model.article.pojos.ApArticle;
import com.shiqu.model.article.pojos.ApUserArticle;
import com.shiqu.model.behavior.dtos.LikesBehaviorDto;
import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.model.common.enums.AppHttpCodeEnum;
import com.shiqu.model.mess.UpdateArticleMess;
import com.shiqu.model.user.pojos.ApUser;
import com.shiqu.utils.thread.AppThreadLocalUtil;
import com.shiqu.utils.thread.UserThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@Slf4j
public class ApLikesBehaviorServiceImpl implements ApLikesBehaviorService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    private ApUserArticleService apUserArticleService;

    @Override
    public ResponseResult like(LikesBehaviorDto dto) {

        //1.检查参数
        if (dto == null || dto.getArticleId() == null || checkParam(dto)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        UpdateArticleMess mess = new UpdateArticleMess();
        mess.setArticleId(dto.getArticleId());
        mess.setType(UpdateArticleMess.UpdateArticleType.LIKES);

        //3.点赞  保存数据
        if (dto.getOperation() == 0) {
            Object obj = cacheService.hGet(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
            if (obj != null) {
                // 删除当前key
                //log.info("删除当前key:{}, {}", dto.getArticleId(), user.getId());
                //cacheService.hDelete(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
                //mess.setAdd(-1);
                return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
            }
            // 保存当前key
            log.info("保存当前key:{} ,{}, {}", dto.getArticleId(), user.getId(), dto);
            cacheService.hPut(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString(), JSON.toJSONString(dto));
            mess.setAdd(1);
        } else {
            // 删除当前key
            log.info("删除当前key:{}, {}", dto.getArticleId(), user.getId());
            cacheService.hDelete(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
            mess.setAdd(-1);
        }

        //调用赋值给OtherBehavior
        UserArticleDto userArticleDto = new UserArticleDto();
        userArticleDto.setArticleId(dto.getArticleId());
        apUserArticleService.saveOrUpdate(userArticleDto, (long)mess.getAdd() * 3);

        //发送消息，数据聚合
        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC,JSON.toJSONString(mess));


        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }

    /**
     * 检查参数
     *
     * @return
     */
    private boolean checkParam(LikesBehaviorDto dto) {
        if (dto.getType() > 2 || dto.getType() < 0 || dto.getOperation() > 1 || dto.getOperation() < 0) {
            return true;
        }
        return false;
    }
}
