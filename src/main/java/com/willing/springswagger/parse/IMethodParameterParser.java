package com.willing.springswagger.parse;

import com.willing.springswagger.models.ParameterModel;

import java.lang.reflect.Parameter;

public interface IMethodParameterParser {
    ParameterModel parse(Parameter parameter, ParameterModel parameterModel);
}
