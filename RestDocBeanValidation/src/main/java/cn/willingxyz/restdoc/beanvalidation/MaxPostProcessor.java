package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;

import javax.validation.constraints.Max;

/**
 * javax.validation.constraints.Max
 */
public class MaxPostProcessor extends AbstractPropertyPostProcessor {
    @Override
    public void postProcessInternal(PropertyModel propertyModel) {
        Max maxAnno = propertyModel.getPropertyItem().getAnnotation(Max.class);
        if (maxAnno != null)
        {
            propertyModel.setDescription(
                    TextUtils.combine(propertyModel.getDescription(),
                            String.format(" (值小于等于%s)", maxAnno.value())
                    )
            );
        }
    }
}
