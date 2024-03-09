package com.pp.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class TraceIdInterceptor  implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String traceId = getTraceId();
        // 将traceId添加到响应头
        response.addHeader("traceId", traceId);
        return true;
    }
    private String getTraceId() {
        return String.format("%s", UUID.randomUUID());
    }
}
