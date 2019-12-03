package cn.willingxyz.restdoc.spring.examples.parameter;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 类型ParameterA类
 */
@Data
public class ParameterA
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
    /**
     * 参数parameterB
     */
    private ParameterB _parameterB;
    /**
     * 参数ParameterBArray数组
     */
    private ParameterB[] _parameterBArray;
    private boolean _hasPassword;
}
