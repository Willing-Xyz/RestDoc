package com.willing.springswagger.parse;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface IMethodParameterResolver {
    boolean isSupport(Parameter parameter);
}
