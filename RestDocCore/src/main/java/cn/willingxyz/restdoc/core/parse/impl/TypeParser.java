package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.models.TypeContext;
import cn.willingxyz.restdoc.core.parse.IPropertyParser;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.IPropertyResolver;
import cn.willingxyz.restdoc.core.parse.ITypeParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TypeParser implements ITypeParser {
    private final IPropertyResolver _propertyResolver;
    private final IPropertyParser _propertyParser;
    private final IPropertyPostProcessor _propertyPostProcessor;

    public TypeParser(IPropertyResolver propertyResolver, IPropertyParser propertyParser, IPropertyPostProcessor propertyPostProcessor) {
        this._propertyResolver = propertyResolver;
        this._propertyParser = propertyParser;
        this._propertyPostProcessor = propertyPostProcessor;
    }

    @Override
    public List<PropertyModel> parse(TypeContext typeContext) {
        Type type = typeContext.getType();
        List<PropertyModel> propertyModels = new ArrayList<>();

        List<PropertyItem> items = this._propertyResolver.resolve(type);
        for (PropertyItem item : items) {
            PropertyModel propertyModel = _propertyParser.parse(item);
            if (propertyModel != null) {
                propertyModel = postProcess(propertyModel, typeContext);
                if (propertyModel != null) {
                    propertyModels.add(propertyModel);
                }
            }
        }
        return propertyModels;
    }

    protected PropertyModel postProcess(PropertyModel propertyModel, TypeContext typeContext) {
        if (_propertyPostProcessor != null) {
            propertyModel = _propertyPostProcessor.postProcess(propertyModel, typeContext);
            if (propertyModel == null) return null;

            if (propertyModel.getChildren() != null && !propertyModel.getChildren().isEmpty()) {
                for (int i = 0; i < propertyModel.getChildren().size(); ++i) {
                    PropertyModel model = propertyModel.getChildren().get(i);
                    model = postProcess(model, typeContext);
                    propertyModel.getChildren().set(i, model);
                }
                propertyModel.getChildren().remove(null);
            }
        }
        return propertyModel;
    }
}
