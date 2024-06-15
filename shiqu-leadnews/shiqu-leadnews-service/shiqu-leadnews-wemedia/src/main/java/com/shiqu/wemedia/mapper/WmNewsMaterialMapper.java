package com.shiqu.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiqu.model.wemedia.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {

     void saveRelations(@Param("materialIds") List<Integer> materialIds,@Param("newsId") Integer newsId, @Param("type")Short type);
}