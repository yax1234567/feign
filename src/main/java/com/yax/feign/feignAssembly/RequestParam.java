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
 * @create 2019-03-07 20:04
 **/
public class RequestParam {
    private String url;
    private Object[] args;
    private CONTENT_TYPE contentType;
    private RequestMethod requestMethod;
    private String[] paramNames;
    private Map<Integer, Header> headerMap;
    private Integer jsonIndex;
    private Map<Integer, PathVariable> pathVariables;
    private Integer headersIndex;
    private List<Integer> fileIndexs;
    public RequestParam(String url, Object[] args, CONTENT_TYPE contentType,RequestMethod requestMethod, String[] paramNames,Map<Integer, Header> headerMap,Integer jsonIndex,Map<Integer, PathVariable> pathVariables,Integer headersIndex,List<Integer> fileIndexs) {
        this.url = url;
        this.args = args;
        this.contentType = contentType;
        this.requestMethod=requestMethod;
        this.paramNames=paramNames;
        this.headerMap=headerMap;
        this.jsonIndex=jsonIndex;
        this.pathVariables=pathVariables;
        this.headersIndex=headersIndex;
        this.fileIndexs=fileIndexs;
    }

    public String getUrl() {
        return url;
    }

    public CONTENT_TYPE getContentType() {
        return contentType;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public Object[] getArgs() {
        return args;
    }

    public String[] getParamNames() {
        return paramNames;
    }

    public Map<Integer, Header> getHeaderMap() {
        return headerMap;
    }

    public Integer getJsonIndex() {
        return jsonIndex;
    }

    public Map<Integer, PathVariable> getPathVariables() {
        return pathVariables;
    }

    public Integer getHeadersIndex() {
        return headersIndex;
    }

    public List<Integer> getFileIndexs() {
        return fileIndexs;
    }
}
