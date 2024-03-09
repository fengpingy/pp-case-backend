package com.pp.domain.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pp.common.enums.system.RoleType;
import com.pp.dao.UserMapper;
import com.pp.domain.service.api.AuthService;
import com.pp.dto.request.LoginRequestDTO;
import com.pp.dto.response.LoginResponseDTO;
import com.pp.entity.UserEntity;
import com.pp.expection.PpExpection;
//import com.moka.jira.JiraAuth;
import com.pp.ldap.LdapAuth;
import com.pp.ldap.LdapResponse;
import com.pp.ldap.Response;
import com.pp.ldap.Role;
import com.pp.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;

import java.util.List;

import static com.pp.common.constants.CommonError.USERNAME_OR_PASSWORD_ERROR;
import static com.pp.common.constants.SystemConst.LDAP_SUCCESS_FLAG;


@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserMapper userMapper;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        // 调用MOKA LDAP 接口
        Response ldapLogin = LdapAuth.ldaplogin(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
        if (!ldapLogin.getMsg().equals(LDAP_SUCCESS_FLAG)) {
            throw new PpExpection(USERNAME_OR_PASSWORD_ERROR);
        }
        // 解析LDAP接口数据
        Object data = ldapLogin.getData();
//        log.info("ladp返回数据为："+data.toString());

        LdapResponse ldapResponse = JSON.parseObject(JSON.toJSONString(data), LdapResponse.class);
//        log.info("获取数据为："+ldapResponse.toString());
        //用户名
        String name = ldapResponse.getUser().getName();
        String realName = ldapResponse.getUser().getReale_name();
        // 角色
        String roleId = "";
        List<Role> roles = ldapResponse.getRoles();
        for (Role role : roles) {
            roleId = role.getId(); // 获取用户角色
        }
        LambdaQueryWrapper<UserEntity> logins = new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, loginRequestDTO.getUsername());
        UserEntity userEntity = userMapper.selectOne(logins);
        //第一次登录
        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setName(name);
        if (userEntity == null) {
            loginResponse.setIsFirstLogin(true);
            UserEntity newUserEntity = new UserEntity();
            newUserEntity.setUsername(name);
            newUserEntity.setNickname(realName);
            newUserEntity.setRoleType(RoleType.nameOf(roleId));
            userMapper.insert(newUserEntity); // 创建映射用户
            loginResponse.setId(newUserEntity.getId());
            loginResponse.setToken(JWTUtils.getJwtToken(String.valueOf(newUserEntity.getId()), newUserEntity.getNickname(), newUserEntity.getRoleType().name(),
                    loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));
            loginResponse.setType(0);
        } else {
            loginResponse.setIsFirstLogin(false);
            loginResponse.setId(userEntity.getId());
            loginResponse.setToken(JWTUtils.getJwtToken(String.valueOf(userEntity.getId()), userEntity.getNickname(), userEntity.getRoleType().name(),
                    loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));
            loginResponse.setType(userEntity.getType());
        }
        return loginResponse;
    }
}
