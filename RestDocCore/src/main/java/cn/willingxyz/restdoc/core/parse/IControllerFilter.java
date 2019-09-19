package cn.willingxyz.restdoc.core.parse;

import java.util.List;

/**
 * 过滤controller
 */
public interface IControllerFilter {
    boolean isSupport(Class clazz);
}
