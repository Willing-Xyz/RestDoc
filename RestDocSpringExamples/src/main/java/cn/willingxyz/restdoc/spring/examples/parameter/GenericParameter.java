package cn.willingxyz.restdoc.spring.examples.parameter;

import lombok.Data;

/**
 * 泛型参数类
 */
@Data
public class GenericParameter<T> {
    /**
     * 参数name
     */
    private String _name;
    /**
     * 参数data
     */
    private T _data;
}
