package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;

import javax.validation.constraints.DecimalMax;

/**
 * javax.validation.constraints.DecimalMax
 */
public class DecimalMaxPostProcessor extends AbstractBeanValidationPropertyPostProcessor {
    @Override
    public PropertyModel postProcessInternal(PropertyModel propertyModel) {
        DecimalMax maxAnno = propertyModel.getPropertyItem().getAnnotation(DecimalMax.class);
        if (maxAnno == null) return propertyModel;

        String hint = "";
        if (maxAnno.inclusive())
            hint += " (值小于等于" + maxAnno.value() + ")";
        else
            hint += " (值小于" + maxAnno.value() + ")";
        propertyModel.setDescription(
                TextUtils.combine(propertyModel.getDescription(), hint)
        );
        return propertyModel;
    }
}
