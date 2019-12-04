package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;
import com.google.auto.service.AutoService;

import javax.validation.constraints.NegativeOrZero;

/**
 * javax.validation.constraints.NegativeOrZero
 */
@AutoService(IPropertyPostProcessor.class)
public class NegativeOrZeroPostProcessor extends AbstractBeanValidationPropertyPostProcessor {
    @Override
    public PropertyModel postProcessInternal(PropertyModel propertyModel) {
        NegativeOrZero negativeAnno = propertyModel.getPropertyItem().getAnnotation(NegativeOrZero.class);
        if (negativeAnno == null) return propertyModel;

        propertyModel.setDescription(TextUtils.combine(
                propertyModel.getDescription(),
                " (值只能为负数或0)"
        ));
        return propertyModel;
    }
}
