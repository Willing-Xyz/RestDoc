package cn.willingxyz.restdoc.jackson;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.models.TypeContext;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.auto.service.AutoService;

import java.util.Arrays;

/**
 * com.fasterxml.jackson.annotation.JsonIgnoreProperties
 */
@AutoService(IPropertyPostProcessor.class)
public class JsonIgnorePropertiesPostProcessor implements IPropertyPostProcessor {
    @Override
    public PropertyModel postProcess(PropertyModel propertyModel, TypeContext typeContext) {
        // 1. 当@JsonIgnoreProperties出现在字段或getter/setter方法上时
        JsonIgnoreProperties jsonIgnoreProperties = propertyModel.getPropertyItem().getAnnotation(JsonIgnoreProperties.class);
        if (jsonIgnoreProperties != null && arrayContains(jsonIgnoreProperties.value(), propertyModel.getName())) {
                return null;
        }
        // 2. 当@JsonIgnoreProperties出现在类上
        Class clazz = propertyModel.getPropertyItem().getDeclaringClass();
        if (clazz != null) {
            jsonIgnoreProperties = (JsonIgnoreProperties) clazz.getDeclaredAnnotation(JsonIgnoreProperties.class);
            if (jsonIgnoreProperties != null && arrayContains(jsonIgnoreProperties.value(), propertyModel.getName())) {
                return null;
            }
        }
        return propertyModel;
    }

    private boolean arrayContains(String[] arr, String value)
    {
        return Arrays.asList(arr).contains(value);
    }
}
