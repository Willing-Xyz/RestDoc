package cn.willingxyz.restdoc.spring.examples.enumeration;

import lombok.Data;

@Data
public class EnumContainer {
    private String _name;
    private EnumA _enumA;
    private EnumA[] _enumAs;
}
