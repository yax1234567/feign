package com.yax.feign.annotation;

import java.lang.annotation.*;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-08 23:23
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Header {
    public String key();
}
