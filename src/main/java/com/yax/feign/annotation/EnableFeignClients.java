package com.yax.feign.annotation;

import com.yax.feign.beanPostProcessor.FeignClientBeanPostProcessor;
import com.yax.feign.feignAssembly.OkhttpClient;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-08 10:29
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({FeignClientBeanPostProcessor.class, OkhttpClient.class})
public @interface EnableFeignClients {
    String[] basePackages() default {};
}
