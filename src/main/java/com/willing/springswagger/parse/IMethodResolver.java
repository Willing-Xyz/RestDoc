package com.willing.springswagger.parse;

import java.lang.reflect.Method;

public interface IMethodResolver {
    boolean isSupport(Method method);
}
