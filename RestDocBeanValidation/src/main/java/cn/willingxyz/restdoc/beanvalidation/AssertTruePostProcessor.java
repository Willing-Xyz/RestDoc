package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;

import javax.validation.constraints.AssertTrue;

/**
 * javax.validation.constraints.AssertTrue
 */
public class AssertTruePostProcessor extends AbstractBeanValidationPropertyPostProcessor {
    @Override
    public PropertyModel postProcessInternal(PropertyModel propertyModel) {
        AssertTrue assertTrueAnno = propertyModel.getPropertyItem().getAnnotation(AssertTrue.class);
        if (assertTrueAnno == null) return propertyModel;

        propertyModel.setDescription(TextUtils.combine(propertyModel.getDescription(), " (值只能为true)"));
        return propertyModel;
    }
}
