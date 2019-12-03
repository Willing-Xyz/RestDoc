package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Max;

/**
 * javax.validation.constraints.DecimalMax
 */
public class DecimalMaxPostProcessor extends AbstractPropertyPostProcessor {
    @Override
    public void postProcessInternal(PropertyModel propertyModel) {
        DecimalMax maxAnno = propertyModel.getPropertyItem().getAnnotation(DecimalMax.class);
        if (maxAnno != null)
        {
            String hint = "";
            if (maxAnno.inclusive())
                hint += " (值小于等于" + maxAnno.value() + ")";
            else
                hint += " (值小于" + maxAnno.value() + ")";
            propertyModel.setDescription(
                    TextUtils.combine(propertyModel.getDescription(), hint)
            );
        }
    }
}
