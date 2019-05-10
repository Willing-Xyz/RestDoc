package com.willing.restdoc.springswagger3.examples.response;

import lombok.Data;

@Data
public class GenericResponse<T> {
    private String _name;
    private T _data;
}
