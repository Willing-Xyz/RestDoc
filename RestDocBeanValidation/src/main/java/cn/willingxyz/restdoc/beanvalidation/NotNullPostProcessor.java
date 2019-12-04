package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyModel;

import javax.validation.constraints.NotNull;

/**
 * javax.validation.constraints.NotNull
 */
public class NotNullPostProcessor extends AbstractBeanValidationPropertyPostProcessor {
    @Override
    public PropertyModel postProcessInternal(PropertyModel propertyModel) {
        NotNull notNullAnno = propertyModel.getPropertyItem().getAnnotation(NotNull.class);
        if (notNullAnno == null)  return propertyModel;

        propertyModel.setRequired(true);

        return propertyModel;
    }
}
