package com.shiqu.model.article.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shiqu.model.article.vos.UserArticleVo;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("ap_user_article")
public class ApUserArticle implements Serializable {
    @TableId(value = "id")
    private Long id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 频道id
     */
    @TableField("channel_id")
    private int channelId;

    /**
     * 频道分值
     */
    @TableField("score")
    private Long score;

    @Override
    public String toString() {
        return "ApUserArticle{" +
                "userId='" + userId + '\'' +
                '}';
    }

}
