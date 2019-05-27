package com.yax.feign.annotation;

import java.lang.annotation.*;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-18 11:06
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Headers {
}
