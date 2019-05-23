package cn.willingxyz.restdoc.springswagger3.examples.parameter;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class ParameterA
{
    private int _id;
    private String _name;
    private ParameterB _parameterB;
    private ParameterB[] _parameterBArray;
}
