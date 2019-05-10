package com.willing.restdoc.core.parse;

import java.lang.reflect.Method;

public interface IMethodResolver {
    /**
     * 给定一个Method，判断该Method是否是HTTP处理接口
     */
    boolean isSupport(Method method);
}
