package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;

import javax.validation.constraints.NotBlank;

/**
 * javax.validation.constraints.NotBlank
 */
public class NotBlankPostProcessor extends AbstractPropertyPostProcessor {
    @Override
    public void postProcessInternal(PropertyModel propertyModel) {
        NotBlank notBlankAnno = propertyModel.getPropertyItem().getAnnotation(NotBlank.class);
        if (notBlankAnno != null)
        {
            propertyModel.setRequired(true);
            propertyModel.setDescription(TextUtils.combine(propertyModel.getDescription(), " (值不能仅包含空白字符)"));
        }
    }
}
