package com.willing.restdoc.core.models;

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

    public static enum HttpMethod
    {
        GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH, TRACE
    }
}
