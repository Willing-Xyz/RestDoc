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
public class AssertTruePostProcessor implements IPropertyPostProcessor {
    @Override
    public void postProcess(PropertyModel propertyModel) {
        AssertTrue assertTrueAnno = propertyModel.getPropertyItem().getAnnotation(AssertTrue.class);
        if (assertTrueAnno != null) {
            propertyModel.setDescription(TextUtils.combine(propertyModel.getDescription(), " (值只能为true)"));
        }
    }
}
