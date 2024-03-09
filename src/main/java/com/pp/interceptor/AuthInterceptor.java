package com.pp.interceptor;

import com.pp.common.UserHolder;
import com.pp.common.annotation.IgnoreAuth;
import com.pp.common.constants.SystemConst;
import com.pp.entity.UserEntity;
import com.pp.expection.PpExpection;
import com.pp.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.pp.common.constants.CommonError.TOKEN_AUTH_ERROR;


/**
 * 认证拦截器
 */
@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        //如果有忽略 直接跳过
        boolean hasIgnoreAuth = ((HandlerMethod) handler).hasMethodAnnotation(IgnoreAuth.class);

        if (hasIgnoreAuth) {
            return true;
        }
        String token = request.getHeader(SystemConst.MOKA_HEADER_TOKEN);
        log.info("token为："+token);
        //如果不生效
        if (token == null || token.equals("")) {
            throw new PpExpection(TOKEN_AUTH_ERROR);
        } else {
            if (JWTUtils.checkToken(token)) {
                UserEntity userEntity = new UserEntity();
                userEntity.setId(Long.valueOf(JWTUtils.getMemberIdByJwtToken(token, SystemConst.ID)));
                userEntity.setNickname(JWTUtils.getMemberIdByJwtToken(token, SystemConst.NICKNAME));
                userEntity.setUsername(JWTUtils.getMemberIdByJwtToken(token, SystemConst.NAME));
                userEntity.setPassword(JWTUtils.getMemberIdByJwtToken(token, SystemConst.PASSWORD));
                UserHolder.set(userEntity);
                return true;
            } else {
                throw new PpExpection(TOKEN_AUTH_ERROR);
            }
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserHolder.remove();
    }

}
