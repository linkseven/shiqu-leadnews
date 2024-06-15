package com.shiqu.behavior.service;

import com.shiqu.model.behavior.dtos.CollectionBehaviorDto;
import com.shiqu.model.behavior.dtos.LikesBehaviorDto;
import com.shiqu.model.common.dtos.ResponseResult;

public interface ApCollectionBehaviorService {
    /**
     * 存储收藏数据
     * @param dto
     * @return
     */
    public ResponseResult collection(CollectionBehaviorDto dto);
}
