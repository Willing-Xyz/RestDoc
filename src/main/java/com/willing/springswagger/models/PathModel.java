package com.willing.springswagger.models;

import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * controller的方法，每个url对应一个
 */
@Data
public class PathModel {
    /**
     * url路径
     */
    private String[] _paths;
    /**
     * HTTP METHOD
     */
    private RequestMethod[] _httpMethods;
    /**
     * 详细描述
     */
    private String _description;
    /**
     * 参数列表
     */
    private List<ParameterModel> _parameters = new ArrayList<>();
    /**
     * 响应
     */
    private List<ResponseModel> _response = new ArrayList<>();
}
