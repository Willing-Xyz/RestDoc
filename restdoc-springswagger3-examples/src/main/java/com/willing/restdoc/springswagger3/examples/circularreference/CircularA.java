package com.willing.restdoc.springswagger3.examples.circularreference;

import lombok.Data;

@Data
public class CircularA {
    private String _name;
    private CircularB _circularB;
}
