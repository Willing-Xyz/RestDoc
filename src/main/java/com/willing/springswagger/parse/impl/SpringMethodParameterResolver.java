package com.willing.springswagger.parse.impl;

import com.willing.springswagger.parse.IMethodParameterResolver;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SpringMethodParameterResolver implements IMethodParameterResolver {
    public static Class[] _classes = new Class[]{
            HttpServletRequest.class
    };
    private final Set<Class> _classSet;

    public SpringMethodParameterResolver()
    {
        _classSet = Arrays.stream(_classes).collect(Collectors.toSet());
    }
    @Override
    public boolean isSupport(Parameter parameter) {
        // eg HttpServletRequest
        if (_classSet.contains(parameter.getType()))
            return false;
        return true;
    }
}
