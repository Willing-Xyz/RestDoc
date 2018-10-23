package com.willing.springswagger.parse;

import java.lang.reflect.Type;

public interface ISwaggerTypeInspector extends ITypeInspector{
    /**
     * 转换为swagger类型
     */
    String toSwaggerType(Type type);

    /**
     * 转换为swagger format
     */
    String toSwaggerFormat(Type type);
}
