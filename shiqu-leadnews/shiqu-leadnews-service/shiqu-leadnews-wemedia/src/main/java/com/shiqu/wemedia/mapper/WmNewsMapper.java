package com.shiqu.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiqu.model.wemedia.pojos.WmNews;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface WmNewsMapper  extends BaseMapper<WmNews> {
    
}