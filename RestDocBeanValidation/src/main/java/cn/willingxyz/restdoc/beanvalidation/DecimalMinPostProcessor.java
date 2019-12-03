package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

/**
 * javax.validation.constraints.DecimalMin
 */
public class DecimalMinPostProcessor implements IPropertyPostProcessor {
    @Override
    public void postProcess(PropertyModel propertyModel) {
        DecimalMin minAnno = propertyModel.getPropertyItem().getAnnotation(DecimalMin.class);
        if (minAnno != null)
        {
            String hint = "";
            if (minAnno.inclusive())
                hint += " (值大于等于" + minAnno.value() + ")";
            else
                hint += " (值大于" + minAnno.value() + ")";
            propertyModel.setDescription(
                    TextUtils.combine(propertyModel.getDescription(), hint)
            );
        }
    }
}
