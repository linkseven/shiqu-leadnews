package com.shiqu.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiqu.model.article.pojos.ApUserArticle;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ApUserArticleMapper extends BaseMapper<ApUserArticle> {
}
