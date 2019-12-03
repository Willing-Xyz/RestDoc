package cn.willingxyz.restdoc.core.parse;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.models.TypeContext;

public interface IPropertyPostProcessor {
    void postProcess(PropertyModel propertyModel, TypeContext typeContext);
}
