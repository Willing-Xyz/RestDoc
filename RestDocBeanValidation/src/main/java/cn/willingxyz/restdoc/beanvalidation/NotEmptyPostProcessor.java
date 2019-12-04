package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;
import com.google.auto.service.AutoService;

import javax.validation.constraints.NotEmpty;

/**
 * javax.validation.constraints.NotEmpty
 */
@AutoService(IPropertyPostProcessor.class)
public class NotEmptyPostProcessor extends AbstractBeanValidationPropertyPostProcessor {
    @Override
    public PropertyModel postProcessInternal(PropertyModel propertyModel) {
        NotEmpty notEmptyAnno = propertyModel.getPropertyItem().getAnnotation(NotEmpty.class);
        if (notEmptyAnno == null)  return propertyModel;

        propertyModel.setRequired(true);
        propertyModel.setDescription(TextUtils.combine(propertyModel.getDescription(), " (值不能仅包含空白字符)"));

        return propertyModel;
    }
}
