package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.IPropertyParser;
import cn.willingxyz.restdoc.core.parse.IPropertyPostProcessor;
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
    public List<PropertyModel> parse(Type type) { // todo 只传递type会缺少一些上下文信息，应该把Parameter，Response等都传递进来
        List<PropertyModel> propertyModels = new ArrayList<>();

        List<PropertyItem> items = this._propertyResolver.resolve(type);
        for (PropertyItem item : items) {
            PropertyModel propertyModel = _propertyParser.parse(item);
            if (propertyModel != null) {
                postProcess(propertyModel);
                propertyModels.add(propertyModel);
            }
        }
        return propertyModels;
    }

    private void postProcess(PropertyModel propertyModel) {
        if (_propertyPostProcessor != null) {
            _propertyPostProcessor.postProcess(propertyModel);
            if (propertyModel.getChildren() != null && !propertyModel.getChildren().isEmpty())
            {
                propertyModel.getChildren().forEach(this::postProcess);
            }
        }
    }
}
