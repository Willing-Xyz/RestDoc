package com.willing.springswagger.models;

import lombok.Data;
import org.omg.CORBA.ParameterMode;

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
    /**
     * 参数描述
     */
    private String _description;
    /**
     * 参数位置，如参数放在查询字符串中或者放到body中
     */
    private ParameterLocation _location;
    /**
     * 参数类型
     */
    private ParameterType _type;
    /**
     * 如果参数类型是枚举或数组类型的时候，该字段表示包含的类型
     */
    private ItemType _itemType;
    /**
     * 如果参数类型是枚举类型，该字段表示枚举的可选值
     */
    private List<Object> _enumValues = new ArrayList<>();
    /**
     * 该参数包含的参数，如参数为对象类型时，包含对象每个key的参数信息
     */
    private List<ParameterModel> _children = new ArrayList<>();

    public enum ParameterLocation
    {
        BODY, QUERY
    }
}
