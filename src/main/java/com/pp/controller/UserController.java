package com.pp.controller;


import com.pp.common.Result;
import com.pp.dto.UserDTO;
import com.pp.dto.request.query.PageDTO;
import com.pp.dto.request.query.page.UserPageQuery;
import com.pp.dto.response.page.PageResponse;
import com.pp.dto.response.page.UserPage;
import com.pp.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/moka/case-platform/user")
@Api(tags = {"用户管理"})
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation("用户列表")
    @PostMapping("/list")
    public Result userList(@RequestBody @Valid PageDTO<UserPageQuery> userPageQueryPageDTO) {
        PageResponse<UserPage> userPagePageResponse = userService.userList(userPageQueryPageDTO);
        return Result.ok().setContent(userPagePageResponse);
    }

    @ApiOperation("用户信息")
    @GetMapping("/{id}")
    public Result user(@PathVariable @Valid Long id) {
        UserDTO user = userService.getUser(id);
        return Result.ok().setContent(user);
    }

    @ApiOperation("用户名获取用户信息")
    @GetMapping("/getUser/{name}")
    public Result userName(@PathVariable @Valid String name) {
        UserDTO user = userService.getUserByName(name);
        return Result.ok().setContent(user);
    }

    @ApiOperation("更新用户角色")
    @PostMapping("/updateRole")
    public Result updateRole(@RequestBody @Valid UserDTO userDTO) {
        int userResponse = userService.updateRoleType(userDTO);
        return Result.ok().setContent(userResponse);
    }
}