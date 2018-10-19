package com.willing.springswagger.parse;

public interface ISwaggerTypeInspector extends ITypeInspector{
    /**
     * 转换为swagger类型
     */
    String toSwaggerType(Class clazz);

    /**
     * 转换为swagger format
     */
    String toSwaggerFormat(Class clazz);
}
