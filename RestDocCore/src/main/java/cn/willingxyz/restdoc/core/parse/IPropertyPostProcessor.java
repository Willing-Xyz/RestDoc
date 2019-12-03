package cn.willingxyz.restdoc.core.parse;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;

public interface IPropertyPostProcessor {
    void postProcess(PropertyModel propertyModel);
}
