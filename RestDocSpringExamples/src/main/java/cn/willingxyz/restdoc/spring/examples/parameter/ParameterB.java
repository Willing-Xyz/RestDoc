package cn.willingxyz.restdoc.spring.examples.parameter;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 类型ParameterB类
 */
@Data
public  class ParameterB
{
    /**
     * 参数id
     */
    private int _id;
    /**
     * 参数name
     */
    @NotNull
    private String _name;
}
