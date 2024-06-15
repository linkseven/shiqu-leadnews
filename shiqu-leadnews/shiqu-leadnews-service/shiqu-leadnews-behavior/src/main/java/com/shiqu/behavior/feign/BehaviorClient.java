package com.shiqu.behavior.feign;

import com.shiqu.apis.behavior.IBehaviorClient;
import com.shiqu.behavior.service.HotArticleService;
import com.shiqu.model.article.vos.HotArticleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class BehaviorClient implements IBehaviorClient {
    @Autowired
    private HotArticleService hotArticleService;

    @PostMapping("/api/v1/article/firstPage")
    public List<HotArticleVo> getFirstPage(){
        List<HotArticleVo> articleBySimilarity = hotArticleService.getArticleBySimilarity();
        return articleBySimilarity;
    }
}
