package com.shiqu.apis.behavior;

import com.shiqu.model.article.vos.HotArticleVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient("leadnews-behavior")
public interface IBehaviorClient {
    @PostMapping("/api/v1/article/firstPage")
    public List<HotArticleVo> getFirstPage() ;
}
