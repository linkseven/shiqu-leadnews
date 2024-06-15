package com.shiqu.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiqu.model.user.pojos.ApUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ApUserMapper extends BaseMapper<ApUser> {
}
