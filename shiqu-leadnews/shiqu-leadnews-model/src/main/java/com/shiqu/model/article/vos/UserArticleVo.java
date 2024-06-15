package com.shiqu.model.article.vos;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserArticleVo implements Comparable<UserArticleVo>, Serializable {
    private int channelId;

    private Long score;

    @Override
    public int compareTo(UserArticleVo o) {
        return score > o.score ? -1 : 1;
    }

    @Override
    public String toString() {
        return "UserArticleVo{" +
                "channelId='" + channelId + '\'' +
                ", score=" + score +
                '}';
    }
}
