package com.willing.restdoc.swagger3;

import com.willing.restdoc.core.parse.ITypeInspector;

import java.lang.reflect.Type;

public interface ISwaggerTypeInspector extends ITypeInspector {
    /**
     * 转换为swagger类型
     */
    String toSwaggerType(Type type);

    /**
     * 转换为swagger format
     */
    String toSwaggerFormat(Type type);
}
