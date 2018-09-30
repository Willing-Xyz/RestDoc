package com.willing.springswagger.parse.impl;

import com.willing.springswagger.parse.IMethodResolver;
import lombok.var;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class SpringMethodResolver implements IMethodResolver {
    private static List<Class<? extends Annotation>> _classes = Arrays.asList(
            RequestMapping.class,
            GetMapping.class,
            PostMapping.class,
            PutMapping.class,
            DeleteMapping.class
    );
    @Override
    public boolean isSupport(Method method) {
        if (method.isSynthetic() || method.isBridge())
            return false;
        var annotations = method.getAnnotations();
        for (var annotation : annotations) {
            var annotationType = annotation.annotationType();

            if (_classes.contains(annotationType))
                return true;
        }
        return false;
    }
}
