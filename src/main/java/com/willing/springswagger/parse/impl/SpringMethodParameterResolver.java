package com.willing.springswagger.parse.impl;

import com.willing.springswagger.parse.IMethodParameterResolver;

import java.lang.reflect.Parameter;

public class SpringMethodParameterResolver implements IMethodParameterResolver {
    @Override
    public boolean isSupport(Parameter parameter) {
        // eg HttpServletRequest
        return true;
    }
}
