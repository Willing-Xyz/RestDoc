package com.willing.springswagger.parse;

import com.github.therapi.runtimejavadoc.ParamJavadoc;
import com.willing.springswagger.models.ParameterModel;

import java.lang.reflect.Parameter;

public interface IMethodParameterParser {
    ParameterModel parse(Parameter parameter, ParamJavadoc paramJavadoc, ParameterModel parameterModel);
}
