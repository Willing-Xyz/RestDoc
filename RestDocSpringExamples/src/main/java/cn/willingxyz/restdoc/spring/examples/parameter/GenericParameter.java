package cn.willingxyz.restdoc.spring.examples.parameter;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 泛型参数类
 */
@Data
public class GenericParameter<T> {
    /**
     * 参数name
     */
    @NotNull
    private String _name;
    /**
     * 参数data
     */
    private T _data;
}
