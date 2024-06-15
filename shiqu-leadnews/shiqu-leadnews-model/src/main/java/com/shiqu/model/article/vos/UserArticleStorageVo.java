package com.shiqu.model.article.vos;

import com.shiqu.model.user.pojos.ApUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserArticleStorageVo {
    private Long userId;
    private List<UserArticleVo> userArticleVos;

    public UserArticleVo find(Integer channelId) {
        for (UserArticleVo userArticleVo : userArticleVos) {
            if (userArticleVo.getChannelId() == channelId) {
                return userArticleVo;
            }
        }
        return null;
    }

    public UserArticleStorageVo(Long userId){
        this.userId = userId;
        userArticleVos = new ArrayList<>();
    }
}
