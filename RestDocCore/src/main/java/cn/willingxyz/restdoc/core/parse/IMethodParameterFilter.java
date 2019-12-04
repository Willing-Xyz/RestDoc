package cn.willingxyz.restdoc.core.parse;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface IMethodParameterFilter {
    boolean isSupport(Parameter parameter);
}
