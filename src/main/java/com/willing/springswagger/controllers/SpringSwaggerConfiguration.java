package com.willing.springswagger.controllers;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class SpringSwaggerConfiguration {

    private List<String> _packages = Arrays.asList("");
}
