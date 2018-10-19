package com.willing.springswagger.parse;

public interface ITypeInspector {
    /**
     * 是否是简单类型
     */
    boolean isSimpleType(Class clazz);

    boolean isSupport(Class clazz);
}
