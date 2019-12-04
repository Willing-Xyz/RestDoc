package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;

import javax.validation.constraints.Min;

/**
 * javax.validation.constraints.Min
 */
public class MinPostProcessor extends AbstractBeanValidationPropertyPostProcessor {
    @Override
    public PropertyModel postProcessInternal(PropertyModel propertyModel) {
        Min minAnno = propertyModel.getPropertyItem().getAnnotation(Min.class);
        if (minAnno == null) return propertyModel;

        propertyModel.setDescription(
                TextUtils.combine(propertyModel.getDescription(),
                        String.format(" (值大于等于%s)", minAnno.value())
                )
        );
        return propertyModel;
    }
}
