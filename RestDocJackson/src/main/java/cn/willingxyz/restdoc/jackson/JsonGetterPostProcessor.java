package cn.willingxyz.restdoc.jackson;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.models.TypeContext;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.google.auto.service.AutoService;

/**
 * com.fasterxml.jackson.annotation.JsonGetter
 */
@AutoService(IPropertyPostProcessor.class)
public class JsonGetterPostProcessor implements IPropertyPostProcessor {
    @Override
    public PropertyModel postProcess(PropertyModel propertyModel, TypeContext typeContext) {
        if (typeContext.getParameter() != null) // != null 表示是参数，不需要处理该注解
            return propertyModel;
        JsonGetter jsonGetterAnno = propertyModel.getPropertyItem().getAnnotation(JsonGetter.class);
        if (jsonGetterAnno == null)
            return propertyModel;

        if (!jsonGetterAnno.value().isEmpty())
        {
            propertyModel.setName(jsonGetterAnno.value());
        }
        return propertyModel;
    }
    // todo 对于只有get的方法仅在作为返回值的时候有效，对于只有set的方法仅在作为参数值的时候有效。
}
