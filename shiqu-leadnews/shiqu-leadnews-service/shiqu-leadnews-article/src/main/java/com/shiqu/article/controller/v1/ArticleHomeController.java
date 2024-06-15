package com.shiqu.article.controller.v1;

import com.shiqu.article.constants.ArticleConstants;
import com.shiqu.article.service.ApArticleService;
import com.shiqu.article.service.HotArticleService;
import com.shiqu.model.article.dtos.ArticleHomeDto;
import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.utils.thread.AppThreadLocalUtil;
import com.shiqu.utils.thread.UserThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/article")
public class ArticleHomeController {

    @Autowired
    private ApArticleService articleService;

    @Autowired
    private HotArticleService hotArticleService;

    /**
     * 加载首页
     * @param dto
     * @return
     */
    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDto dto){
        //        return apArticleService.load(dto, ArticleConstants.LOADTYPE_LOAD_MORE);
        hotArticleService.computeHotArticle();
        return articleService.load2(dto, ArticleConstants.LOADTYPE_LOAD_MORE,true);
    }

    /**
     * 加载最新
     * @param dto
     * @return
     */
    @PostMapping("/loadnew")
    public ResponseResult loadNew(@RequestBody ArticleHomeDto dto){
        return articleService.load(dto, ArticleConstants.LOADTYPE_LOAD_NEW);
    }

    /**
     * 加载更多
     * @param dto
     * @return
     */
    @PostMapping("/loadmore")
    public ResponseResult loadMore(@RequestBody ArticleHomeDto dto){
        return articleService.load(dto, ArticleConstants.LOADTYPE_LOAD_MORE);
    }
}
