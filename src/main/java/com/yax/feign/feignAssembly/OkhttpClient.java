package com.yax.feign.feignAssembly;

import com.alibaba.fastjson.JSONObject;
import com.yax.feign.annotation.Header;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-07 20:33
 **/
public class OkhttpClient implements Client {
    private static final Logger logger = LoggerFactory.getLogger(OkhttpClient.class);
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    private static final MediaType MutilPart_Form_Data =MediaType.parse("multipart/form-data; charset=utf-8");

    private static final MediaType Form =MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private static OkHttpClient okHttpClient =
            new OkHttpClient.Builder()
                    .connectTimeout(10,TimeUnit.SECONDS)
                    .readTimeout(10,TimeUnit.SECONDS)
                    .writeTimeout(10,TimeUnit.SECONDS)
                    .build();
    @Override
    public Response execute(RequestParam requestParam) throws Exception {
       Object[] args= requestParam.getArgs();
       CONTENT_TYPE contentType= requestParam.getContentType();
       String url= requestParam.getUrl();
       RequestMethod requestMethod=requestParam.getRequestMethod();
       String[] paramNames=requestParam.getParamNames();
        Map<Integer, Header> headerMap=requestParam.getHeaderMap();
        Map<Integer, PathVariable> pathVariables=requestParam.getPathVariables();
        Integer headersIndex=requestParam.getHeadersIndex();
        List<Integer> fileIndexs =requestParam.getFileIndexs();
        for(Map.Entry<Integer,PathVariable> entry:pathVariables.entrySet()){
            Integer index=entry.getKey();
            PathVariable pathVariable=entry.getValue();
            if(pathVariable!=null&&!StringUtils.isEmpty(pathVariable.value())){
                url=url.replace(pathVariable.value(),String.valueOf(args[index]));
            }
        }
       MediaType mediaType=Form;
        byte[] content=null;
        String paramForm="";
       if(contentType.equals(CONTENT_TYPE.JSON)){
           mediaType=JSON;
           content= JSONObject.toJSONString(args[requestParam.getJsonIndex()]).getBytes();
       }
        FormBody.Builder builder=new FormBody.Builder();
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MutilPart_Form_Data);
        StringBuilder sb = new StringBuilder();
       if(contentType.equals(CONTENT_TYPE.FORM)){
           mediaType=Form;
           if(args!=null&&args.length!=0) {
               for (int i = 0; i < args.length; i++) {
                   if(headersIndex!=null) {
                       if (i == headersIndex) {
                           continue;
                       }
                   }
                   if(fileIndexs.size()!=0){
                       if(fileIndexs.contains(i)){
                           continue;
                       }
                   }
                   if (args[i] != null&&headerMap.get(i)==null&&pathVariables.get(i)==null) {
                       builder.add(paramNames[i], args[i].toString());
                       multipartBuilder.addFormDataPart(paramNames[i], args[i].toString());
                       sb.append("&" + paramNames[i] + "=" + args[i]);
                   }
               }
               paramForm=sb.toString().substring(1);
           }
       }
       if(contentType.equals(CONTENT_TYPE.MULTIPART_FORM)){
           mediaType=MutilPart_Form_Data;
           for(Integer fileIndex:fileIndexs){
               if(args[fileIndex]!=null) {
                   if (args[fileIndex] instanceof MultipartFile) {
                       MultipartFile file = (MultipartFile) args[fileIndex];
                       multipartBuilder.addFormDataPart(paramNames[fileIndex], file.getOriginalFilename(), RequestBody.create(MutilPart_Form_Data, file.getBytes()));
                   }
                   if (args[fileIndex] instanceof byte[]) {
                       multipartBuilder.addFormDataPart(paramNames[fileIndex], paramNames[fileIndex], RequestBody.create(MutilPart_Form_Data, (byte[]) args[fileIndex]));
                   }
               }
           }
       }
        RequestBody requestBody =null;
       if(requestMethod==RequestMethod.POST) {
           if(mediaType==Form) {
               requestBody = builder.build();
           }
           if(mediaType==JSON){
               requestBody = RequestBody.create(mediaType, content);
           }
           if(mediaType==MutilPart_Form_Data){
               requestBody=multipartBuilder.build();
           }
       }
        if(requestMethod==RequestMethod.GET) {
            if(!StringUtils.isEmpty(paramForm)){
                if(!url.contains("?")) {
                    url += "?" + paramForm;
                }else{
                    if(url.endsWith("?")) {
                        url += paramForm;
                    }else{
                        url +="&"+paramForm;
                    }
                }
            }
        }
        okhttp3.Request.Builder build= new okhttp3.Request.Builder().url(url);
        headerMap.forEach((k,v)->{
            if(v!=null&&!StringUtils.isEmpty(v.key())){
                build.addHeader(v.key(),String.valueOf(args[k]));
            }
        });
        if(headersIndex!=null) {
            Map<String, String> hearders = (Map<String, String>) args[headersIndex];
            hearders.forEach((k,v)->{
                build.addHeader(k,v);
            });
        }
        okhttp3.Request okHttpRequest=build.method(requestMethod==RequestMethod.GET?"GET":"POST",requestBody).build();
        Call call=okHttpClient.newCall(okHttpRequest);
        okhttp3.Response response=call.execute();
        byte[] body= response.body().bytes();
        String back=new String(body,"utf-8");
        return new Response(back,response.code(),body);
    }

}
