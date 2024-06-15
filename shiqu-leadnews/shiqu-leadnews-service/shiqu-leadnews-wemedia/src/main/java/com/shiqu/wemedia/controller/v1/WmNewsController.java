package com.shiqu.wemedia.controller.v1;

import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.model.wemedia.dtos.WmNewsDto;
import com.shiqu.model.wemedia.dtos.WmNewsPageReqDto;
import com.shiqu.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {
    @Autowired
    private WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult findAll(@RequestBody WmNewsPageReqDto dto){
        return  wmNewsService.findAll(dto);
    }

    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto){
        return wmNewsService.submitNews(dto);
    }

    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsDto dto){
        return wmNewsService.downOrUp(dto);
    }

    @GetMapping("/del_news/{id}")
    public ResponseResult delNews(@PathVariable("id")Integer id){
        return wmNewsService.delNews(id);
    }
}