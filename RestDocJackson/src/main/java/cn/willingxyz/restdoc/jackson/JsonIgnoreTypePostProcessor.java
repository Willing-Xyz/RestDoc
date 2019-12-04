package cn.willingxyz.restdoc.jackson;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.models.TypeContext;
import cn.willingxyz.restdoc.core.parse.IPropertyPostProcessor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;

import java.lang.reflect.Type;

/**
 * com.fasterxml.jackson.annotation.JsonIgnoreType
 */
public class JsonIgnoreTypePostProcessor implements IPropertyPostProcessor {
    @Override
    public PropertyModel postProcess(PropertyModel propertyModel, TypeContext typeContext) {

        Type propType = propertyModel.getPropertyItem().getPropertyType();
        if (propType instanceof Class)
        {
            Class<?> clazz = (Class<?>) propType;
            JsonIgnoreType jsonIgnoreTypeAnno = clazz.getAnnotation(JsonIgnoreType.class);
            if (jsonIgnoreTypeAnno != null && jsonIgnoreTypeAnno.value()) {
                return null;
            }
        }

        if (propertyModel.getParentPropertyItem() == null) return propertyModel;

        Type type = propertyModel.getParentPropertyItem().getPropertyType();
        if (!(type instanceof Class)) return propertyModel;

        Class<?> clazz = (Class) type;
        JsonIgnoreType jsonIgnoreTypeAnno = clazz.getAnnotation(JsonIgnoreType.class);
        if (jsonIgnoreTypeAnno == null)
            return propertyModel;

        if (jsonIgnoreTypeAnno.value()) return null;
        return propertyModel;
    }
}
