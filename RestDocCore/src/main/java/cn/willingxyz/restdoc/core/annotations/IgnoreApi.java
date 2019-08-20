package cn.willingxyz.restdoc.core.annotations;

import java.lang.annotation.*;

/**
 * 被该注解标注的类和方法不会进入文档中
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreApi {
}
