package com.willing.restdoc.core.models;

import lombok.Data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Data
public class PropertyModel {
    /**
     * 属性名
     */
    private String _name;
    /**
     * 是否必须
     */
    private Boolean _required;
    private Type _propertyType;
    private boolean _isArray;
    /**
     * 属性描述
     */
    private String _description;

    /**
     * 该属性包含的属性，如属性为对象类型时，包含对象每个key的参数信息
     */
    private List<PropertyModel> _children = new ArrayList<>();
}
