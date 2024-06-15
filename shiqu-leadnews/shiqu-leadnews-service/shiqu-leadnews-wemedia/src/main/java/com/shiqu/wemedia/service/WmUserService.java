package com.shiqu.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.model.wemedia.dtos.WmLoginDto;
import com.shiqu.model.wemedia.pojos.WmUser;

public interface WmUserService extends IService<WmUser> {

    /**
     * 自媒体端登录
     * @param dto
     * @return
     */
    public ResponseResult login(WmLoginDto dto);

}