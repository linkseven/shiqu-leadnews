package com.shiqu.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.model.wemedia.dtos.WmNewsDto;
import com.shiqu.model.wemedia.dtos.WmNewsPageReqDto;
import com.shiqu.model.wemedia.pojos.WmNews;

public interface WmNewsService extends IService<WmNews> {

    /**
     * 查询文章
     * @param dto
     * @return
     */
    public ResponseResult findAll(WmNewsPageReqDto dto);
    public ResponseResult submitNews(WmNewsDto dto);
    public ResponseResult downOrUp(WmNewsDto dto);
    public ResponseResult delNews(Integer id);
}