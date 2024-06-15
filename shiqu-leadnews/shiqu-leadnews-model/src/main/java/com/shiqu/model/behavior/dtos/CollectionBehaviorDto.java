package com.shiqu.model.behavior.dtos;

import lombok.Data;

@Data
public class CollectionBehaviorDto {
    // 文章、动态、评论等ID
    Long articleId;

    /**
     * 收藏操作方式
     * 0 收藏
     * 1 取消收藏
     */
    Short type;
}
