package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;

import javax.validation.constraints.AssertFalse;

/**
 * javax.validation.constraints.AssertFalse
 */
public class AssertFalsePostProcessor extends AbstractPropertyPostProcessor {
    @Override
    public void postProcessInternal(PropertyModel propertyModel) {
        AssertFalse assertFalseAnno = propertyModel.getPropertyItem().getAnnotation(AssertFalse.class);
        if (assertFalseAnno != null) {
            propertyModel.setDescription(TextUtils.combine(propertyModel.getDescription(), " (值只能为false)"));
        }
    }
}
