package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.models.TypeContext;
import cn.willingxyz.restdoc.core.parse.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.ITypeInspector;

public class RequiredPropertyPostProcessor implements IPropertyPostProcessor {
    private final ITypeInspector _typeInspector;

    public RequiredPropertyPostProcessor(ITypeInspector typeInspector)
    {
        _typeInspector = typeInspector;
    }
    @Override
    public void postProcess(PropertyModel propertyModel, TypeContext typeContext) {
        if (propertyModel.getPropertyItem().getPropertyType() instanceof Class)
        {
            Class clazz = (Class)propertyModel.getPropertyItem().getPropertyType();
            if (clazz.isPrimitive())
            {
                propertyModel.setRequired(true);
            }
        }
    }
}
