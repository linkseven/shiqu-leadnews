package com.shiqu.behavior.controller.v1;

import com.shiqu.behavior.service.ApCollectionBehaviorService;
import com.shiqu.model.behavior.dtos.CollectionBehaviorDto;
import com.shiqu.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/collection_behavior")
public class ApCollectionBehaviorController {
    @Autowired
    private ApCollectionBehaviorService apCollectionBehaviorService;

    @PostMapping
    public ResponseResult collection(@RequestBody CollectionBehaviorDto dto) {
        return apCollectionBehaviorService.collection(dto);
    }
}
