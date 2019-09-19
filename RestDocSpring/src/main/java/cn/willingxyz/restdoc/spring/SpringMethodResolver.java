package cn.willingxyz.restdoc.spring;

import cn.willingxyz.restdoc.core.parse.IMethodResolver;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class SpringMethodResolver implements IMethodResolver {
    private static Logger _logger = LoggerFactory.getLogger(SpringMethodResolver.class);

    private static List<Class<? extends Annotation>> _classes = Arrays.asList(
            RequestMapping.class,
            GetMapping.class,
            PostMapping.class,
            PutMapping.class,
            DeleteMapping.class,
            PatchMapping.class
    );

    @Override
    public boolean isSupport(Method method) {
        if (method.isSynthetic() || method.isBridge())
            return false;



        // 如果方法和类上都没有ResponseBody，返回false
        if (!AnnotatedElementUtils.hasAnnotation(method, ResponseBody.class) &&
            !AnnotatedElementUtils.hasAnnotation(method.getDeclaringClass(), ResponseBody.class))
        {
                return false;
        }
        var annotations = method.getAnnotations();
        for (var annotation : annotations) {
            var annotationType = annotation.annotationType();

            if (_classes.contains(annotationType))
                return true;
        }
        return false;
    }
}
