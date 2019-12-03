package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;

import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;

/**
 * javax.validation.constraints.NegativeOrZero
 */
public class NegativeOrZeroPostProcessor implements IPropertyPostProcessor {
    @Override
    public void postProcess(PropertyModel propertyModel) {
        NegativeOrZero negativeAnno = propertyModel.getPropertyItem().getAnnotation(NegativeOrZero.class);
        if (negativeAnno != null) {
            propertyModel.setDescription(TextUtils.combine(
                    propertyModel.getDescription(),
                    " (值只能为负数或0)"
            ));
        }
    }
}
