package com.shiqu.behavior.service;

import com.shiqu.model.behavior.dtos.ReadBehaviorDto;
import com.shiqu.model.common.dtos.ResponseResult;

public interface ApReadBehaviorService {

    /**
     * 保存阅读行为
     * @param dto
     * @return
     */
    public ResponseResult readBehavior(ReadBehaviorDto dto);
}
