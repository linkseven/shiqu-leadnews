package com.shiqu.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.model.common.enums.AppHttpCodeEnum;
import com.shiqu.model.user.dtos.LoginDto;
import com.shiqu.model.user.pojos.ApUser;
import com.shiqu.user.mapper.ApUserMapper;
import com.shiqu.user.service.ApUserService;
import com.shiqu.utils.common.AppJwtUtil;
import com.shiqu.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {
    @Override
    public ResponseResult login(LoginDto dto) {
        //1.正常登录 用户名和密码
        if (StringUtils.isNotBlank(dto.getPhone()) && StringUtils.isNotBlank(dto.getPassword())) {
            //1.1 根据手机号查询用户
            ApUser dbUser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, dto.getPhone()));
            if (dbUser == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "此用户不存在,请注册");
            }

            //1.2 比对密码
            String salt = dbUser.getSalt();
            String password = dto.getPassword();
            String pswd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if (!pswd.equals(dbUser.getPassword())){
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }

            //1.3 返回数据 jwt
            String token = AppJwtUtil.getToken(dbUser.getId().longValue());
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            dbUser.setSalt("");
            dbUser.setPassword("");
            map.put("user",dbUser);
            return ResponseResult.okResult(map);
        } else {
            //2.游客登录
            String token = AppJwtUtil.getToken(0L);
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            return ResponseResult.okResult(map);
        }
    }
}
