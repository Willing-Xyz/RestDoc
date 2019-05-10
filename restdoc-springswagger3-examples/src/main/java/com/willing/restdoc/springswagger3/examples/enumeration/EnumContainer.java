package com.willing.restdoc.springswagger3.examples.enumeration;

import lombok.Data;

@Data
public class EnumContainer {
    private String _name;
    private EnumA _enumA;
    private EnumA[] _enumAs;
}
