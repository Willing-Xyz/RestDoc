package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.IPropertyPostProcessor;

import javax.validation.constraints.Null;

/**
 * javax.validation.constraints.Null
 */
public class NullPostProcessor implements IPropertyPostProcessor {
    @Override
    public void postProcess(PropertyModel propertyModel) {
        Null nullAnno = propertyModel.getPropertyItem().getAnnotation(Null.class);
        if (nullAnno != null) {
            propertyModel.setRequired(false);
        }
    }
}
