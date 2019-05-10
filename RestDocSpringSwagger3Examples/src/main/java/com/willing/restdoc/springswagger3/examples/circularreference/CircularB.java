package com.willing.restdoc.springswagger3.examples.circularreference;

import lombok.Data;

@Data
public class CircularB {
    private String _name;
    private CircularA _circularA;
}
