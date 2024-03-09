package com.pp.config;


import com.pp.interceptor.AuthInterceptor;
import com.pp.interceptor.TraceIdInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Resource
    private AuthInterceptor authInterceptor;

    @Resource
    private TraceIdInterceptor traceIdInterceptor;



    @Value("${login.auth.enable}")
    private Boolean enableAuth;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加traceId 拦截器
        registry.addInterceptor(traceIdInterceptor);
        //权限拦截器
        if (enableAuth){
            registry.addInterceptor(authInterceptor)
                    .addPathPatterns("/**")
                    .order(5)
                    .excludePathPatterns("/swagger-ui/**",
                            "/swagger-resources/**",
                            "/v2/**",
                            "/doc.html"
                    );
        }

    }

    /**
     * 跨域解决
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(false)
                .allowedMethods("POST","PUT","GET","DELETE","OPTIONS")
                .allowedOrigins("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //配置拦截器访问静态资源
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
