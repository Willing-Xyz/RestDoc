package cn.willingxyz.restdoc.jackson;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.models.TypeContext;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.service.AutoService;

/**
 * com.fasterxml.jackson.annotation.JsonProperty
 */
@AutoService(IPropertyPostProcessor.class)
public class JsonPropertyPostProcessor implements IPropertyPostProcessor {
    @Override
    public PropertyModel postProcess(PropertyModel propertyModel, TypeContext typeContext) {
        JsonProperty jsonProperty = propertyModel.getPropertyItem().getAnnotation(JsonProperty.class);
        if (jsonProperty == null) return propertyModel;

        if (!jsonProperty.value().isEmpty()) {
            propertyModel.setName(jsonProperty.value());
        }
        if (jsonProperty.access() == JsonProperty.Access.READ_ONLY && typeContext.inOut() == TypeContext.InOut.IN)
        {
            return null;
        }
        if (jsonProperty.access() == JsonProperty.Access.WRITE_ONLY && typeContext.inOut() == TypeContext.InOut.OUT)
        {
            return null;
        }
        return propertyModel;
    }
}
