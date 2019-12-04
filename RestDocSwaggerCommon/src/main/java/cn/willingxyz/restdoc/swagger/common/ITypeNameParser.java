package cn.willingxyz.restdoc.swagger.common;

import java.lang.reflect.Type;

/**
 * 根据Type获取类型的名称（如简化类名）
 */
public interface ITypeNameParser {
    String parse(Type type);
}
