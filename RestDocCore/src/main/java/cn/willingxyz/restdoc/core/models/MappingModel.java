package cn.willingxyz.restdoc.core.models;

import lombok.Data;

@Data
public class MappingModel {
    /**
     * url路径
     */
    private String[] _paths = new String[]{};
    /**
     * HTTP METHOD
     */
    private HttpMethod[] _httpMethods = new HttpMethod[]{};

    public enum HttpMethod
    {
        GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH, TRACE
    }
}
