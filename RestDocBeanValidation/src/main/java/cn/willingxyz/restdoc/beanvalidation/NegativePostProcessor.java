package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;

import javax.validation.constraints.Negative;

/**
 * javax.validation.constraints.Negative
 */
public class NegativePostProcessor extends AbstractPropertyPostProcessor {
    @Override
    public void postProcessInternal(PropertyModel propertyModel) {
        Negative negativeAnno = propertyModel.getPropertyItem().getAnnotation(Negative.class);
        if (negativeAnno != null) {
            propertyModel.setDescription(TextUtils.combine(
                    propertyModel.getDescription(),
                    " (值只能为负数，不包括0)"
            ));
        }
    }
}
