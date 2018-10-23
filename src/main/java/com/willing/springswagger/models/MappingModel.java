package com.willing.springswagger.models;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestMethod;

@Data
public class MappingModel {
    /**
     * url路径
     */
    private String[] _paths = new String[]{};
    /**
     * HTTP METHOD
     */
    private RequestMethod[] _httpMethods = new RequestMethod[]{};
}
