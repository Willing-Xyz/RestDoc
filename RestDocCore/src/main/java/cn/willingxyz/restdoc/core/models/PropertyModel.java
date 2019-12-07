package cn.willingxyz.restdoc.core.models;

import lombok.Data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Data
public class PropertyModel {
    /**
     * 父级PropertyItem。可能为null
     * 比如：
     * A类里有一个B类型的字段b，类型B里有字段x
     * 那么x的parentPropertyItem就是字段b
     */
    private PropertyItem _parentPropertyItem;
    /**
     * 关联的PropertyItem
     */
    private PropertyItem _propertyItem;

    /**
     * 属性名
     */
    private String _name;
    /**
     * 是否必须
     */
    private boolean _required;
    private Type _propertyType;
    private boolean _isArray;
    /**
     * 属性描述
     */
    private String _description;
    /**
     * 例子
     */
    private String _example;

    /**
     * 该属性包含的属性，如属性为对象类型时，包含对象每个key的参数信息
     */
    private List<PropertyModel> _children = new ArrayList<>();

    /**
     * 枚举的可选值
     */
    private List<String> _enums;
}
