package com.yax.feign.beanPostProcessor;


import com.yax.feign.annotation.EnableFeignClients;
import com.yax.feign.annotation.FeignClient;
import com.yax.feign.annotation.Header;
import com.yax.feign.annotation.Headers;
import com.yax.feign.feignAssembly.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-07 17:00
 **/
public class FeignClientBeanPostProcessor implements InstantiationAwareBeanPostProcessor, ApplicationContextAware, BeanDefinitionRegistryPostProcessor, EnvironmentAware, PriorityOrdered {
    private static final Logger logger = LoggerFactory.getLogger(FeignClientBeanPostProcessor.class);
    private static final Map<Method, FeignProperty> feignPropertys= Collections.synchronizedMap(new HashMap<>());
    private ApplicationContext applicationContext;
    private Environment environment;
    @Override
    public int getOrder() {
        return 0;
    }
    @Override
    public void setEnvironment(Environment environment) {
        this.environment=environment;
    }
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {

    }
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        FeignScanner scanner = new FeignScanner((BeanDefinitionRegistry) configurableListableBeanFactory);
        scanner.setResourceLoader(this.applicationContext);
        String[] classNames=configurableListableBeanFactory.getBeanNamesForAnnotation(EnableFeignClients.class);
        if(classNames!=null&&classNames.length!=0){
            BeanDefinition beanDefinition=configurableListableBeanFactory.getBeanDefinition(classNames[0]);
            try {
                Class mainApplicationClass=Class.forName(beanDefinition.getBeanClassName().split("\\$")[0]);
                EnableFeignClients enableFeignClients= (EnableFeignClients) mainApplicationClass.getAnnotation(EnableFeignClients.class);
                String[] basePackages=enableFeignClients.basePackages();
                if(basePackages.length!=0) {
                    scanner.scan(basePackages);
                    logger.info("scanning "+ Arrays.toString(basePackages));
                    return;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        String feignScanBasePackage=environment.getProperty("feign.Scan.basePackage");
        logger.info("scanning "+feignScanBasePackage);
        scanner.scan(feignScanBasePackage);
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if(isFeignClient(beanClass)){
            FeignClient feignClient=beanClass.getDeclaredAnnotation(FeignClient.class);
            Class clz=feignClient.fallback();
            String url=feignClient.url();
            addFeignPropertys(beanClass);
            FeignFactory feignFactory=new FeignFactory(url,clz,beanClass,applicationContext) ;
            return feignFactory.newInstance();
        }
        return null;
    }
    public boolean isFeignClient(Class<?> beanClass){
       FeignClient feignClient=beanClass.getDeclaredAnnotation(FeignClient.class);
        if(feignClient==null){
            return false;
        }
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
    public void addFeignPropertys(Class<?> beanClass){
      Method[] methods= beanClass.getDeclaredMethods();
        CONTENT_TYPE contentType=CONTENT_TYPE.FORM;
      for(Method method:methods){
          RequestMapping requestMapping= method.getAnnotation(RequestMapping.class);
          String[] apiName=requestMapping.value();
          RequestMethod[] requestMethod= requestMapping.method();
          String[] paramNames=getParameterNames(method);
          Parameter[] parameters= method.getParameters();
          Map<Integer,Header> headers=new HashMap<>();
          Map<Integer, PathVariable> pathVariables =new HashMap<>();
          Integer jsonIndex=0;
          List<Integer> fileIndexs=new ArrayList<>();
          Integer headersIndex=null;
          for(int i=0;i<parameters.length;i++){
              Parameter parameter=parameters[i];
              Header header= parameter.getAnnotation(Header.class);
              PathVariable pathVariable= parameter.getAnnotation(PathVariable.class);
              headers.put(i,header);
              pathVariables.put(i,pathVariable);
              RequestBody requestBody= parameter.getAnnotation(RequestBody.class);
              Headers headers1=parameter.getAnnotation(Headers.class);
              if(headers1!=null){
                  headersIndex=i;
              }
              if(requestBody!=null){
                  contentType=CONTENT_TYPE.JSON;
                  jsonIndex=i;
              }
              Object paramClass= parameter.getType();
              if(paramClass==MultipartFile.class){
                  contentType=CONTENT_TYPE.MULTIPART_FORM;
                  fileIndexs.add(i);
              }
              org.springframework.web.bind.annotation.RequestParam requestParam= parameter.getAnnotation(org.springframework.web.bind.annotation.RequestParam.class);
              if(requestParam!=null&&!StringUtils.isEmpty(requestParam.value())){
                  paramNames[i]=requestParam.value();
              }

          }

          feignPropertys.put(method,new FeignProperty(apiName,requestMethod,headers,contentType,paramNames,jsonIndex,pathVariables,headersIndex,fileIndexs));
      }
    }
    public static FeignProperty  getFeignPropertyByMethod(Method method){
        return feignPropertys.get(method);
    }
    public String[] getParameterNames(Method method){
        ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        return discoverer.getParameterNames(method);
    }


}
