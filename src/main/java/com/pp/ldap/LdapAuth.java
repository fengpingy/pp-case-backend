package com.pp.ldap;

import com.alibaba.fastjson2.JSON;
import com.pp.common.OkhttpClient;
import com.pp.dto.request.LoginRequestDTO;

public class LdapAuth {


    /**
     * 统一认证地址
     */
    public final static String MOKA_UNIFY_AUTH = "https://moka-autotest-actuator.mokahr.com/api/gateway/auth/get_jwt";


    public final static OkhttpClient client;

    static {
        client = OkhttpClient.newsInstance();
    }

    /**
     * 认证
     *
     * @param username
     * @param password
     * @return
     */
    public static Response ldaplogin(String username, String password) {
        String post = client.post(MOKA_UNIFY_AUTH, buildBody(username, password));
        return JSON.parseObject(post, Response.class);
    }


    /**
     * 构建请求体
     * @param username
     * @param password
     * @return
     */
    public static String buildBody(String username, String password) {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername(username);
        loginRequestDTO.setPassword(password);
        return JSON.toJSONString(loginRequestDTO);
    }


}
