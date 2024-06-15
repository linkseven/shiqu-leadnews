package com.shiqu.search.service;

import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.model.search.dtos.UserSearchDto;

/**
 * <p>
 * 联想词表 服务类
 * </p>
 *
 * @author itshiqu
 */
public interface ApAssociateWordsService {

    /**
     联想词
     @param userSearchDto
     @return
     */
    ResponseResult findAssociate(UserSearchDto userSearchDto);

}