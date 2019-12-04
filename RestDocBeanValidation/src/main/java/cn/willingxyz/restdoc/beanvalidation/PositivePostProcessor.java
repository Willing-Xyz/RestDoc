package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;

import javax.validation.constraints.Positive;

/**
 * javax.validation.constraints.Positive
 */
public class PositivePostProcessor extends AbstractBeanValidationPropertyPostProcessor {
    @Override
    public PropertyModel postProcessInternal(PropertyModel propertyModel) {
        Positive positiveAnno = propertyModel.getPropertyItem().getAnnotation(Positive.class);
        if (positiveAnno == null)  return propertyModel;

        propertyModel.setDescription(TextUtils.combine(
                propertyModel.getDescription(),
                " (值只能为正数，不包括0)"
        ));
        return propertyModel;
    }
}
