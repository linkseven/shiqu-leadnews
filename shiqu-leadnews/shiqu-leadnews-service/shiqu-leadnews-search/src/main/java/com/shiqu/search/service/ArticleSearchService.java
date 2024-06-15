package com.shiqu.search.service;

import com.shiqu.model.search.dtos.UserSearchDto;
import com.shiqu.model.common.dtos.ResponseResult;

import java.io.IOException;

public interface ArticleSearchService {

    /**
     ES文章分页搜索
     @return
     */
    ResponseResult search(UserSearchDto userSearchDto) throws IOException;
}