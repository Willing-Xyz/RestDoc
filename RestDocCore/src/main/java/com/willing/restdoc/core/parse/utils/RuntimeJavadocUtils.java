package com.willing.restdoc.core.parse.utils;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;

import java.util.ArrayList;
import java.util.List;

public class RuntimeJavadocUtils {
    public static List<MethodJavadoc> getAllMethodJavadoc(Class clazz)
    {
        List<MethodJavadoc> methods = new ArrayList<>();

        while (clazz != Object.class)
        {
            ClassJavadoc classJavadoc = getClassJavadoc(clazz);

            methods.addAll(classJavadoc.getMethods());

            clazz = clazz.getSuperclass();
        }

        return methods;
    }

    public static ClassJavadoc getClassJavadoc(Class clazz)
    {
        return RuntimeJavadoc.getJavadoc(clazz);
    }
}
