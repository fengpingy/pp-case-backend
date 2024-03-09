package com.pp.common.aspect;

import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;

import static java.util.UUID.randomUUID;


/**
 * 日志aspect
 */
@Component
@Slf4j
@Aspect
@Order(10)
public class LogAspect {

    /**
     * 请求id
     */
    public static final ThreadLocal<String> SEQ_HOLDER = ThreadLocal.withInitial(randomUUID()::toString);
    /**
     * 时间
     */
    public static final ThreadLocal<Date> START_HOLDER = ThreadLocal.withInitial(Date::new);
    /**
     * 计时
     */
    ThreadLocal<Long> startTime = new ThreadLocal<>();


    @Pointcut("execution (* com.pp.controller.*.*(..)) || execution (* com.pp.domain.controller.*.*(..))")
    public void logPoint() {

    }


    @Before("logPoint()")
    public void beforeLogPrint(JoinPoint point) {
        // 开始计时
        startTime.set(System.currentTimeMillis());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        // 打印请求内容
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));//获取请求头中的User-Agent
        log.info("traceId: {}", response.getHeader("traceId"));
        log.info("接口路径：{}" , request.getRequestURL().toString());
        log.info("浏览器：{}", userAgent.getBrowser().toString());
        log.info("浏览器版本：{}",userAgent.getBrowserVersion());
        log.info("操作系统: {}", userAgent.getOperatingSystem().toString());
        log.info("IP : {}" , request.getRemoteAddr());
        log.info("请求类型：{}", request.getMethod());
        log.info("类方法 : " + point.getSignature().getDeclaringTypeName() + "." + point.getSignature().getName());
        log.info("请求参数 :  " + Arrays.toString(point.getArgs()));

    }

    @AfterReturning(pointcut = "logPoint()", returning = "ret")
    public void afterReturnLogPrint(Object ret) {
        SEQ_HOLDER.remove();
        START_HOLDER.remove();
        // 处理完请求后返回内容
        log.info("方法返回值：{}" , ret);
        log.info("方法执行时间：{}毫秒", (System.currentTimeMillis() - startTime.get()));
    }

    @AfterThrowing(value = "logPoint()", throwing = "throwable")
    public void afterThrowingLogPrint(Throwable throwable) {
        SEQ_HOLDER.remove();
        START_HOLDER.remove();
    }

}
