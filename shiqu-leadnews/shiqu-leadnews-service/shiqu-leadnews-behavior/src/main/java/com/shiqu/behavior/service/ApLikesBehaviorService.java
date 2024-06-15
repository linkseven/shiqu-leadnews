package com.shiqu.behavior.service;

import com.shiqu.model.behavior.dtos.LikesBehaviorDto;
import com.shiqu.model.common.dtos.ResponseResult;

public interface ApLikesBehaviorService {

    /**
     * 存储喜欢数据
     * @param dto
     * @return
     */
    public ResponseResult like(LikesBehaviorDto dto);
}
