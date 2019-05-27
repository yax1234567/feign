package com.yax.feign.feignAssembly;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-07 20:00
 **/
public interface Client {
    Response execute(RequestParam requestParam) throws Exception;

       class Response{
         private String response;
         private Integer code;
         private byte[] bytes;

        public Response(String response, Integer code, byte[] bytes) {
            this.response = response;
            this.code = code;
            this.bytes = bytes;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }
    }
}
