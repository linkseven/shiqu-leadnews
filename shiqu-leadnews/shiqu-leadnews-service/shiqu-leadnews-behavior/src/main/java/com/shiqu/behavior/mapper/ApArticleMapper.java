package com.shiqu.behavior.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiqu.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ApArticleMapper extends BaseMapper<ApArticle> {
}
