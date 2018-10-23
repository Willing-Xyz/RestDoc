package com.willing.springswagger.parse;

import java.lang.reflect.Type;

public interface ITypeInspector {
    /**
     * 是否是简单类型
     */
    boolean isSimpleType(Type type);

    boolean isSupport(Type type);
}
