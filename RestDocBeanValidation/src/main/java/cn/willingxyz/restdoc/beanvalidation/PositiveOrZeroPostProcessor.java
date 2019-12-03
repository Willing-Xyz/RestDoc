package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * javax.validation.constraints.PositiveOrZero
 */
public class PositiveOrZeroPostProcessor extends AbstractPropertyPostProcessor {
    @Override
    public void postProcessInternal(PropertyModel propertyModel) {
        PositiveOrZero positiveOrZeroAnno = propertyModel.getPropertyItem().getAnnotation(PositiveOrZero.class);
        if (positiveOrZeroAnno != null) {
            propertyModel.setDescription(TextUtils.combine(
                    propertyModel.getDescription(),
                    " (值只能为正数，包括0)"
            ));
        }
    }
}
