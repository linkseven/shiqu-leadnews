package com.shiqu.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiqu.model.article.pojos.ApArticleConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ApArticleConfigMapper extends BaseMapper<ApArticleConfig> {
}
