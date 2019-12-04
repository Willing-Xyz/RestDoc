package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;

import javax.validation.constraints.Negative;

/**
 * javax.validation.constraints.Negative
 */
public class NegativePostProcessor extends AbstractBeanValidationPropertyPostProcessor {
    @Override
    public PropertyModel postProcessInternal(PropertyModel propertyModel) {
        Negative negativeAnno = propertyModel.getPropertyItem().getAnnotation(Negative.class);
        if (negativeAnno == null) return propertyModel;


        propertyModel.setDescription(TextUtils.combine(
                propertyModel.getDescription(),
                " (值只能为负数，不包括0)"
        ));
        return propertyModel;
    }
}
