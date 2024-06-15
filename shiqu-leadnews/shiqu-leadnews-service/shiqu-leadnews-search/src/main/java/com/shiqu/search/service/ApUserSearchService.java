package com.shiqu.search.service;

import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.model.search.dtos.HistorySearchDto;

public interface ApUserSearchService {

    /**
     * 保存用户搜索历史记录
     * @param keyword
     * @param userId
     */
    public void insert(String keyword,Integer userId);

    /**
     查询搜索历史
     @return
     */
    ResponseResult findUserSearch();

    /**
     删除搜索历史
     @param historySearchDto
     @return
     */
    ResponseResult delUserSearch(HistorySearchDto historySearchDto);
}