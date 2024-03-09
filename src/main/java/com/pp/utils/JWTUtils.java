package com.pp.utils;


import com.pp.common.constants.SystemConst;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;


import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.pp.common.constants.SystemConst.*;

/**
 * token工具类
 */
public class JWTUtils {


    /**
     * 生成token
     *
     * @param id
     * @param username
     * @return
     */
    public static String getJwtToken(String id, String username,String role,String name, String password) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + SystemConst.TOKEN_EXPIRE_TIME))
                .claim(SystemConst.ID, id)
                .claim(NICKNAME, username)
                .claim(ROLE,role)
                .claim(NAME,name)
                .claim(PASSWORD,password)
                .signWith(SignatureAlgorithm.HS256, SystemConst.SECURITY_STRING)
                .compact();
    }

    /**
     * 检查token是否生效
     *
     * @param jwtToken
     * @return
     */
    public static boolean checkToken(String jwtToken) {
        if (StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(SystemConst.SECURITY_STRING).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 判断token是否存在与有效
     *
     * @param request
     * @return
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            // 从http请求头中获取token字符串
            String jwtToken = request.getHeader(SystemConst.MOKA_HEADER_TOKEN);
            if (StringUtils.isEmpty(jwtToken)) return false;
            Jwts.parser().setSigningKey(SystemConst.SECURITY_STRING).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 解析id
     *
     * @param request
     * @return
     */
    public static String getMemberIdByJwtToken(HttpServletRequest request, String key) {
        String jwtToken = request.getHeader(SystemConst.MOKA_HEADER_TOKEN);
        if (StringUtils.isEmpty(jwtToken)) return SystemConst.NULL_EMPTY_STRING;
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SystemConst.SECURITY_STRING).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return (String) claims.get(key);
    }


    public static String getMemberIdByJwtToken(String jwt, String value) {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SystemConst.SECURITY_STRING).parseClaimsJws(jwt);
        Claims claims = claimsJws.getBody();
        return (String) claims.get(value);
    }

}
