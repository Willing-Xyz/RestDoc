package cn.willingxyz.restdoc.beanvalidation;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.IPropertyResolver;
import cn.willingxyz.restdoc.core.parse.RestDocParseConfig;
import cn.willingxyz.restdoc.core.parse.impl.PropertyParser;

import javax.validation.constraints.NotNull;

/**
 * javax.validation.constraints.NotNull
 */
public class NotNullPostProcessor extends AbstractPropertyPostProcessor {
    @Override
    public void postProcessInternal(PropertyModel propertyModel) {
        NotNull notNullAnno = propertyModel.getPropertyItem().getAnnotation(NotNull.class);
        if (notNullAnno != null)
        {
            propertyModel.setRequired(true);
        }
    }
}
