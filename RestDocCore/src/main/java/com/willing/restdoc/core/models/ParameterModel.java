package com.willing.restdoc.core.models;

import lombok.Data;
import org.omg.CORBA.ParameterMode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Data
public class ParameterModel {
    /**
     * 参数名
     */
    private String _name;
    /**
     * 是否必须
     */
    private boolean _required;
    private Type _parameterType;
    /**
     * 参数描述
     */
    private String _description;
    /**
     * 参数位置，如参数放在查询字符串中或者放到body中
     */
    private ParameterLocation _location;
    private boolean _isArray;

    /**
     * 该参数包含的参数，如参数为对象类型时，包含对象每个key的参数信息
     */
    private List<PropertyModel> _children = new ArrayList<>();

    public enum ParameterLocation
    {
        BODY, QUERY, PATH, HEADER, FILE;
    }
}
