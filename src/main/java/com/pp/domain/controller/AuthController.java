package com.pp.domain.controller;


import com.pp.common.Result;
import com.pp.common.annotation.IgnoreAuth;
import com.pp.domain.service.api.AuthService;
import com.pp.dto.request.LoginRequestDTO;
import com.pp.dto.response.LoginResponseDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/moka/case-platform/auth")
@Api(tags = { "认证" })
public class AuthController {

    @Resource
    private AuthService authService;

    @ApiOperation("登录")
    @PostMapping("/login")
    @IgnoreAuth
    public Result mokaLogin(@RequestBody LoginRequestDTO loginRequestDTO){
        LoginResponseDTO login = authService.login(loginRequestDTO);
        return Result.ok().setContent(login);
    }



    @ApiOperation("退出登录")
    @PostMapping("/logout")
    @IgnoreAuth
    public Result mokaLogout(){
        return Result.ok();
    }

}
