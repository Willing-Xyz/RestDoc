package com.willing.springswagger.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReturnModel {

    private Class _returnClass;
    /**
     * 描述
     */
    private String _description;
    /**
     * 如果参数类型是枚举或数组类型的时候，该字段表示包含的类型
     */
    private ItemType _itemType;

    /**
     * 该参数包含的参数，如参数为对象类型时，包含对象每个key的参数信息
     */
    private List<ParameterModel> _children = new ArrayList<>();

    public enum ParameterLocation
    {
        BODY, QUERY
    }
}
