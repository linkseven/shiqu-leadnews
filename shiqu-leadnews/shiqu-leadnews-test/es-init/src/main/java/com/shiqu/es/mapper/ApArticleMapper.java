package com.shiqu.es.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiqu.es.pojo.SearchArticleVo;
import com.shiqu.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {

    public List<SearchArticleVo> loadArticleList();

}
