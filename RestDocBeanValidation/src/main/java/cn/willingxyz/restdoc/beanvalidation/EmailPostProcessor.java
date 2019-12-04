package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;

import javax.validation.constraints.Email;

/**
 * javax.validation.constraints.Email
 * 不支持指定正则表达式
 */
public class EmailPostProcessor extends AbstractBeanValidationPropertyPostProcessor {
    @Override
    public PropertyModel postProcessInternal(PropertyModel propertyModel) {
        Email emailAnno = propertyModel.getPropertyItem().getAnnotation(Email.class);
        if (emailAnno == null) return propertyModel;

        propertyModel.setDescription(TextUtils.combine(propertyModel.getDescription(), " (值为Email格式)"));

        return propertyModel;
    }
}
