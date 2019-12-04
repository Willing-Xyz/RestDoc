package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;
import com.google.auto.service.AutoService;

import javax.validation.constraints.DecimalMin;

/**
 * javax.validation.constraints.DecimalMin
 */
@AutoService(IPropertyPostProcessor.class)
public class DecimalMinPostProcessor extends AbstractBeanValidationPropertyPostProcessor {
    @Override
    public PropertyModel postProcessInternal(PropertyModel propertyModel) {
        DecimalMin minAnno = propertyModel.getPropertyItem().getAnnotation(DecimalMin.class);
        if (minAnno == null) return propertyModel;

        String hint = "";
        if (minAnno.inclusive())
            hint += " (值大于等于" + minAnno.value() + ")";
        else
            hint += " (值大于" + minAnno.value() + ")";
        propertyModel.setDescription(
                TextUtils.combine(propertyModel.getDescription(), hint)
        );
        return propertyModel;
    }
}
