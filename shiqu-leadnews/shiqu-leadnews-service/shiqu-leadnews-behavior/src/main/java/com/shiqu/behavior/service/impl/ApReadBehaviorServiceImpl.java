package com.shiqu.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shiqu.behavior.mapper.ApArticleMapper;
import com.shiqu.behavior.mapper.ApUserArticleMapper;
import com.shiqu.behavior.service.ApReadBehaviorService;
import com.shiqu.behavior.service.ApUserArticleService;
import com.shiqu.common.constants.BehaviorConstants;
import com.shiqu.common.constants.HotArticleConstants;
import com.shiqu.common.redis.CacheService;
import com.shiqu.model.article.dtos.UserArticleDto;
import com.shiqu.model.article.pojos.ApArticle;
import com.shiqu.model.article.pojos.ApUserArticle;
import com.shiqu.model.behavior.dtos.ReadBehaviorDto;
import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.model.common.enums.AppHttpCodeEnum;
import com.shiqu.model.mess.UpdateArticleMess;
import com.shiqu.model.user.pojos.ApUser;
import com.shiqu.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class ApReadBehaviorServiceImpl implements ApReadBehaviorService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    private ApUserArticleService apUserArticleService;

    @Override
    public ResponseResult readBehavior(ReadBehaviorDto dto) {
        //1.检查参数
        if (dto == null || dto.getArticleId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //更新阅读次数
        String readBehaviorJson = (String) cacheService.hGet(BehaviorConstants.READ_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
        if (StringUtils.isNotBlank(readBehaviorJson)) {
            ReadBehaviorDto readBehaviorDto = JSON.parseObject(readBehaviorJson, ReadBehaviorDto.class);
            dto.setCount((short) (readBehaviorDto.getCount() + dto.getCount()));
        }
        //调用赋值给OtherBehavior
        UserArticleDto userArticleDto = new UserArticleDto();
        userArticleDto.setArticleId(dto.getArticleId());
        apUserArticleService.saveOrUpdate(userArticleDto, (long)1);
        // 保存当前key
        log.info("保存当前key:{} {} {}", dto.getArticleId(), user.getId(), dto);
        cacheService.hPut(BehaviorConstants.READ_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString(), JSON.toJSONString(dto));

        //发送消息，数据聚合
        UpdateArticleMess mess = new UpdateArticleMess();
        mess.setArticleId(dto.getArticleId());
        mess.setType(UpdateArticleMess.UpdateArticleType.VIEWS);
        mess.setAdd(1);
        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC,JSON.toJSONString(mess));

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
