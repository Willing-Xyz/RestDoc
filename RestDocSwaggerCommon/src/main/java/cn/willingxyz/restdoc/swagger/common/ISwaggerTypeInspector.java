package cn.willingxyz.restdoc.swagger.common;

import java.lang.reflect.Type;

public interface ISwaggerTypeInspector{
    /**
     * 转换为swagger类型
     */
    String toSwaggerType(Type type);

    /**
     * 转换为swagger format
     */
    String toSwaggerFormat(Type type);
}
