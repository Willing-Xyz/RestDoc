package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import com.google.auto.service.AutoService;

import javax.validation.constraints.NotNull;

/**
 * javax.validation.constraints.NotNull
 */
@AutoService(IPropertyPostProcessor.class)
public class NotNullPostProcessor extends AbstractBeanValidationPropertyPostProcessor {
    @Override
    public PropertyModel postProcessInternal(PropertyModel propertyModel) {
        NotNull notNullAnno = propertyModel.getPropertyItem().getAnnotation(NotNull.class);
        if (notNullAnno == null)  return propertyModel;

        propertyModel.setRequired(true);

        return propertyModel;
    }
}
