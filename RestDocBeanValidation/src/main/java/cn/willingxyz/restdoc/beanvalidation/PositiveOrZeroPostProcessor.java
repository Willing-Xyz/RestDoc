package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;
import com.google.auto.service.AutoService;

import javax.validation.constraints.PositiveOrZero;

/**
 * javax.validation.constraints.PositiveOrZero
 */
@AutoService(IPropertyPostProcessor.class)
public class PositiveOrZeroPostProcessor extends AbstractBeanValidationPropertyPostProcessor {
    @Override
    public PropertyModel postProcessInternal(PropertyModel propertyModel) {
        PositiveOrZero positiveOrZeroAnno = propertyModel.getPropertyItem().getAnnotation(PositiveOrZero.class);
        if (positiveOrZeroAnno == null)  return propertyModel;

        propertyModel.setDescription(TextUtils.combine(
                propertyModel.getDescription(),
                " (值只能为正数，包括0)"
        ));
        return propertyModel;
    }
}
