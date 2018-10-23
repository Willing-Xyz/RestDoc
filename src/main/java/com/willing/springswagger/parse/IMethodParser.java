package com.willing.springswagger.parse;

import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.willing.springswagger.models.PathModel;

import java.lang.reflect.Method;

public interface IMethodParser {
    /**
     * 解析Method到Path
     */
    PathModel parse(Method method, MethodJavadoc methodJavadoc, PathModel pathModel);
}
