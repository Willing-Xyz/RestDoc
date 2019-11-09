package cn.willingxyz.restdoc.core.parse;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.parse.utils.ReflectUtils;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 从type中解析出属性列表
 */
public interface IPropertyResolver {
    /**
     * 解析类型包含的属性
     */
    List<PropertyItem> resolve(Type type);
}
