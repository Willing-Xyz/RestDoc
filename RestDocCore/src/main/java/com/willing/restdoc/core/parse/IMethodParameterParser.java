package com.willing.restdoc.core.parse;

import com.github.therapi.runtimejavadoc.ParamJavadoc;
import com.willing.restdoc.core.models.ParameterModel;

import java.lang.reflect.Parameter;

public interface IMethodParameterParser {
    ParameterModel parse(Parameter parameter, ParamJavadoc paramJavadoc, ParameterModel parameterModel);
    boolean isSupport(Parameter parameter);
}
