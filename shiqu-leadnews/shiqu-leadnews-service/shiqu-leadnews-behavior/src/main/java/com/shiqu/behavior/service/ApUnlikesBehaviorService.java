package com.shiqu.behavior.service;

import com.shiqu.model.behavior.dtos.UnLikesBehaviorDto;
import com.shiqu.model.common.dtos.ResponseResult;

/**
 * <p>
 * APP不喜欢行为表 服务类
 * </p>
 *
 * @author itshiqu
 */
public interface ApUnlikesBehaviorService {

    /**
     * 不喜欢
     * @param dto
     * @return
     */
    public ResponseResult unLike(UnLikesBehaviorDto dto);

}