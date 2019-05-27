package com.yax.feign.feignAssembly;

import com.yax.feign.annotation.Header;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-07 17:35
 **/
public class FeignProperty {
    private String[] apiName;
    private RequestMethod[]  requestmethod;
    private Map<Integer, Header> headers;
    private CONTENT_TYPE contentType;
    private String[] paramNames;
    private Map<Integer, PathVariable> pathVariable;
    private Integer jsonIndex;
    private List<Integer> fileIndexs;
    private Integer headersIndex;
    public FeignProperty(String[] apiName, RequestMethod[] requestmethod,  Map<Integer,Header> headers,CONTENT_TYPE contentType,String[] paramNames,Integer jsonIndex,Map<Integer, PathVariable> pathVariable,Integer headersIndex,List<Integer> fileIndexs) {
        this.apiName = apiName;
        this.requestmethod = requestmethod;
        this.headers = headers;
        this.contentType=contentType;
        this.paramNames=paramNames;
        this.jsonIndex=jsonIndex;
        this.pathVariable=pathVariable;
        this.headersIndex=headersIndex;
        this.fileIndexs=fileIndexs;
    }

    public String[] getApiName() {
        return apiName;
    }



    public RequestMethod[] getRequestmethod() {
        return requestmethod;
    }



    public Map<Integer,Header> getHeaders() {
        return headers;
    }


    public CONTENT_TYPE getContentType() {
        return contentType;
    }

    public String[] getParamNames() {
        return paramNames;
    }

    public Integer getJsonIndex() {
        return jsonIndex;
    }

    public Map<Integer, PathVariable> getPathVariable() {
        return pathVariable;
    }

    public Integer getHeadersIndex() {
        return headersIndex;
    }

    public List<Integer> getFileIndexs() {
        return fileIndexs;
    }
}
