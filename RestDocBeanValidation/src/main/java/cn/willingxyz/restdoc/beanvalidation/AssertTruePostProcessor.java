package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;

/**
 * javax.validation.constraints.AssertTrue
 */
public class AssertTruePostProcessor extends AbstractPropertyPostProcessor {
    @Override
    public void postProcessInternal(PropertyModel propertyModel) {
        AssertTrue assertTrueAnno = propertyModel.getPropertyItem().getAnnotation(AssertTrue.class);
        if (assertTrueAnno != null) {
            propertyModel.setDescription(TextUtils.combine(propertyModel.getDescription(), " (值只能为true)"));
        }
    }
}
