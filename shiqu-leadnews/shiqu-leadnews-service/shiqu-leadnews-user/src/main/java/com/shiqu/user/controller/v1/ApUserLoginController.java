package com.shiqu.user.controller.v1;

import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.model.user.dtos.LoginDto;
import com.shiqu.model.user.pojos.ApUser;
import com.shiqu.user.service.ApUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/login")
@Api(value = "app用户登录", tags = "app用户登录")
public class ApUserLoginController {

    @Autowired
    private ApUserService apUserService;

    @PostMapping("/login_auth")
    @ApiOperation("用户登录")
    public ResponseResult login(@RequestBody LoginDto dto){
        return apUserService.login(dto);
    }
}
