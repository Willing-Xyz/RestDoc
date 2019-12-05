package cn.willingxyz.restdoc.core.annotations;

import java.lang.annotation.*;

/**
 * 用于指定 参数、属性、字段、返回值的example
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Example {
    String value() default "";
}
