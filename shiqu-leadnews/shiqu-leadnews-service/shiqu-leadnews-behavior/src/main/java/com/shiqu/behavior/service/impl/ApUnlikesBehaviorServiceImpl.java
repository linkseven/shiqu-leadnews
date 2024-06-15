package com.shiqu.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.shiqu.behavior.service.ApUnlikesBehaviorService;
import com.shiqu.common.constants.BehaviorConstants;
import com.shiqu.common.redis.CacheService;
import com.shiqu.model.behavior.dtos.UnLikesBehaviorDto;
import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.model.common.enums.AppHttpCodeEnum;
import com.shiqu.model.user.pojos.ApUser;
import com.shiqu.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * APP不喜欢行为表 服务实现类
 * </p>
 *
 * @author itshiqu
 */
@Slf4j
@Service
public class ApUnlikesBehaviorServiceImpl implements ApUnlikesBehaviorService {

    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseResult unLike(UnLikesBehaviorDto dto) {

        if (dto.getArticleId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        if (dto.getType() == 0) {
            log.info("保存当前key:{} ,{}, {}", dto.getArticleId(), user.getId(), dto);
            cacheService.hPut(BehaviorConstants.UN_LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString(), JSON.toJSONString(dto));
        } else {
            log.info("删除当前key:{} ,{}, {}", dto.getArticleId(), user.getId(), dto);
            cacheService.hDelete(BehaviorConstants.UN_LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}