package cn.willingxyz.restdoc.core.parse.postprocessor.impl;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.models.TypeContext;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;

public class RequiredPropertyPostProcessor implements IPropertyPostProcessor {


    @Override
    public PropertyModel postProcess(PropertyModel propertyModel, TypeContext typeContext) {
        if (propertyModel.getPropertyItem().getPropertyType() instanceof Class)
        {
            Class clazz = (Class)propertyModel.getPropertyItem().getPropertyType();
            if (clazz.isPrimitive())
            {
                propertyModel.setRequired(true);
            }
        }
        return propertyModel;
    }
}
