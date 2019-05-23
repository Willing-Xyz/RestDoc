package cn.willingxyz.restdoc.springswagger3.examples.parameter;

import lombok.Data;

@Data
public class GenericParameter<T> {
    private String _name;
    private T _data;
}
