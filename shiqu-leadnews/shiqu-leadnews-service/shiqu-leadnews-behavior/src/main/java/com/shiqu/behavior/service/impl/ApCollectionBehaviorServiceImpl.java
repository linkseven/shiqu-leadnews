package com.shiqu.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.shiqu.behavior.service.ApCollectionBehaviorService;
import com.shiqu.common.constants.BehaviorConstants;
import com.shiqu.common.constants.HotArticleConstants;
import com.shiqu.common.redis.CacheService;
import com.shiqu.model.behavior.dtos.CollectionBehaviorDto;
import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.model.common.enums.AppHttpCodeEnum;
import com.shiqu.model.mess.UpdateArticleMess;
import com.shiqu.model.user.pojos.ApUser;
import com.shiqu.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 * APP收藏行为表 服务实现类
 * </p>
 *
 * @author itshiqu
 */
@Slf4j
@Service
public class ApCollectionBehaviorServiceImpl implements ApCollectionBehaviorService {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Override
    public ResponseResult collection(CollectionBehaviorDto dto) {
        if (dto.getArticleId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        UpdateArticleMess mess = new UpdateArticleMess();
        mess.setArticleId(dto.getArticleId());
        mess.setType(UpdateArticleMess.UpdateArticleType.COLLECTION);

        if (dto.getType() == 0) {
            Object obj = cacheService.hGet(BehaviorConstants.COLLECTION_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
            if (obj != null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "已收藏");
            }
            log.info("保存当前key:{} ,{}, {}", dto.getArticleId(), user.getId(), dto);
            cacheService.hPut(BehaviorConstants.COLLECTION_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString(), JSON.toJSONString(dto));
        } else {
            // 删除当前key
            log.info("删除当前key:{}, {}", dto.getArticleId(), user.getId());
            cacheService.hDelete(BehaviorConstants.COLLECTION_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
            mess.setAdd(-1);
        }

        //发送消息，数据聚合
        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC,JSON.toJSONString(mess));

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
