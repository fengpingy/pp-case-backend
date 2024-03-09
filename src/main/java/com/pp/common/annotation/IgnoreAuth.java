package com.pp.common.annotation;


import java.lang.annotation.*;

/**
 * 忽略认证
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreAuth {
}
