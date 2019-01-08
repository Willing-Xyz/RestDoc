package com.willing.restdoc.core.parse.impl;

import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.willing.restdoc.core.models.PathModel;
import com.willing.restdoc.core.parse.utils.FormatUtils;
import com.willing.restdoc.core.parse.IMethodParser;

import java.lang.reflect.Method;

public class JavadocMethodParser implements IMethodParser {
    @Override
    public PathModel parse(Method method, MethodJavadoc methodJavadoc, PathModel pathModel) {
        if (methodJavadoc != null)
        {
            pathModel.setDescription(FormatUtils.format(methodJavadoc.getComment()));
        }
        return pathModel;
    }
}
