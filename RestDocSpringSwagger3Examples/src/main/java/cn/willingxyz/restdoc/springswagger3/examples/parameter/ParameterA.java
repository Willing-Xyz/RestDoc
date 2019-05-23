package cn.willingxyz.restdoc.springswagger3.examples.parameter;

import lombok.Data;

@Data
public class ParameterA
{
    private int _id;
    private String _name;
    private ParameterB _parameterB;
    private ParameterB[] _parameterBArray;
}
