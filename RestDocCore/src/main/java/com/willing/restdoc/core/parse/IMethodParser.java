package com.willing.restdoc.core.parse;

import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.willing.restdoc.core.models.PathModel;

import java.lang.reflect.Method;

public interface IMethodParser {
    /**
     * 解析Method到Path
     */
    PathModel parse(Method method, MethodJavadoc methodJavadoc, PathModel pathModel);
}
