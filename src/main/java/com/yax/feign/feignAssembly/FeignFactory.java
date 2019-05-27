package com.yax.feign.feignAssembly;

import com.alibaba.fastjson.JSONObject;
import com.yax.feign.annotation.Header;
import com.yax.feign.beanPostProcessor.FeignClientBeanPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-07 17:15
 **/
public class FeignFactory implements InvocationHandler {
    private String url;
    private Class fallback;
    private Class targetClass;
    private ApplicationContext applicationContext;
    private static final Logger logger = LoggerFactory.getLogger(FeignFactory.class);
    public FeignFactory(String url, Class fallback, Class targetClass, ApplicationContext applicationContext) {
        this.url = url;
        this.fallback = fallback;
        this.applicationContext=applicationContext;
        this.targetClass=targetClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if(isEquals(method)){
               if(!proxy.getClass().isAssignableFrom(args[0].getClass())){
                   return false;
               }
               if(proxy.hashCode()!=args[0].hashCode())
                   return false;
                return true;
            }
            if(isHashCode(method)){
                return this.hashCode();
            }
            if(isToString(method)){
                return "jdk动态代理类 "+this.toString();
            }

        Object obj;
       Class<?> back= method.getReturnType();
        FeignProperty feignProperty= FeignClientBeanPostProcessor.getFeignPropertyByMethod(method);
        String address=url+(feignProperty.getApiName()[0].contains("/")?feignProperty.getApiName()[0]:"/"+feignProperty.getApiName()[0]);
        if(address.startsWith("/")){
            address=address.substring(1);
        }
        RequestMethod requestMethod=feignProperty.getRequestmethod().length!=0?feignProperty.getRequestmethod()[0]:RequestMethod.POST;
        CONTENT_TYPE contentType=feignProperty.getContentType();
        String[] paramNames=feignProperty.getParamNames();
        Map<Integer, Header> headerMap=feignProperty.getHeaders();
        Map<Integer, PathVariable> pathVariables =feignProperty.getPathVariable();
        Integer jsonIndex=feignProperty.getJsonIndex();
        Integer headersIndex=feignProperty.getHeadersIndex();
        List<Integer> fileIndexs=feignProperty.getFileIndexs();
        RequestParam requestParam=new RequestParam(address,args,contentType,requestMethod,paramNames,headerMap,jsonIndex,pathVariables,headersIndex,fileIndexs);
        Client client=applicationContext.getBean(Client.class);
        Client.Response response= client.execute(requestParam);
        if(response.getCode()==200){
                if(back==byte[].class){
                    obj=response.getBytes();
                }else if(back==String.class){
                    return response.getResponse();
                } else {
                    try {
                        obj = JSONObject.parseObject(response.getResponse(), back);
                    }catch (Exception e){
                        logger.info("远程请求异常"+e.toString());
                        if(fallback==void.class){
                            throw new Exception("fegin 失败回调没有设置");
                        }
                        Object operator=  applicationContext.getBean(fallback);
                        obj= method.invoke(operator,args);
                    }
                }
            }else{
            logger.info("远程服务器响应异常"+response.getResponse());
                if(fallback==void.class){
                    throw new Exception("fegin 失败回调没有设置");
                }
                Object operator=  applicationContext.getBean(fallback);
                obj= method.invoke(operator,args);
        }
        return obj;
    }
    public Object newInstance(){
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{targetClass},this);
    }
    private boolean isEquals(Method method){
        if("equals".equals(method.getName())){
            return true;
        }
        return false;
    }
    private boolean isToString(Method method){
        if("toString".equals(method.getName())){
            return true;
        }
        return false;
    }
    private boolean isHashCode(Method method){
        if("hashCode".equals(method.getName())){
            return true;
        }
        return false;
    }
}
