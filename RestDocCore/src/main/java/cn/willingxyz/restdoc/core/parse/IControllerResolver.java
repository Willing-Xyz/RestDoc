package cn.willingxyz.restdoc.core.parse;

import java.util.List;

/**
 * 获取可处理的类
 */
public interface IControllerResolver {
    List<Class> getClasses();
}
