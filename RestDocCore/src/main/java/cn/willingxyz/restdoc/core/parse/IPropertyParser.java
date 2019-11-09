package cn.willingxyz.restdoc.core.parse;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.utils.ReflectUtils;

/**
 * 解析属性
 */
public interface IPropertyParser {
    /**
     * 如果返回null，表示忽略该属性
     */
    PropertyModel parse(PropertyItem item);
}
