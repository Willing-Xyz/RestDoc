package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyModel;

import javax.validation.constraints.Null;

/**
 * javax.validation.constraints.Null
 */
public class NullPostProcessor extends AbstractBeanValidationPropertyPostProcessor {
    @Override
    public PropertyModel postProcessInternal(PropertyModel propertyModel) {
        Null nullAnno = propertyModel.getPropertyItem().getAnnotation(Null.class);
        if (nullAnno == null)  return propertyModel;

        propertyModel.setRequired(false);

        return propertyModel;
    }
}
