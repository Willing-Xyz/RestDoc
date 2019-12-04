package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;
import com.google.auto.service.AutoService;

import javax.validation.constraints.Max;

/**
 * javax.validation.constraints.Max
 */
@AutoService(IPropertyPostProcessor.class)
public class MaxPostProcessor extends AbstractBeanValidationPropertyPostProcessor {
    @Override
    public PropertyModel postProcessInternal(PropertyModel propertyModel) {
        Max maxAnno = propertyModel.getPropertyItem().getAnnotation(Max.class);
        if (maxAnno == null) return propertyModel;

        propertyModel.setDescription(
                TextUtils.combine(propertyModel.getDescription(),
                        String.format(" (值小于等于%s)", maxAnno.value())
                )
        );
        return propertyModel;
    }
}
