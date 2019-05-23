package cn.willingxyz.restdoc.spring.parameter.resolver;

import cn.willingxyz.restdoc.core.parse.IMethodParameterResolver;
import lombok.var;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestAttribute;

import java.lang.reflect.Parameter;

/**
 * 基于注解的参数过滤，即如果参数上声明了指定的注解，则不解析该参数
 */
public class SpringAnnotationParameterResolver implements IMethodParameterResolver {

    public static Class[] _classes = new Class[]{
            CookieValue.class,
            RequestAttribute.class,
            Value.class
    };

    @Override
    public boolean isSupport(Parameter parameter) {
        for (var clazz : _classes)
        {
            if (AnnotatedElementUtils.hasAnnotation(parameter, clazz))
                return false;
        }
        return true;
    }
}
