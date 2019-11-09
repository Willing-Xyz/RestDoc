package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.IPropertyParser;
import cn.willingxyz.restdoc.core.parse.IPropertyResolver;
import cn.willingxyz.restdoc.core.parse.ITypeParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TypeParser implements ITypeParser {
    private final IPropertyResolver _propertyResolver;
    private final IPropertyParser _propertyParser;

    public TypeParser(IPropertyResolver propertyResolver, IPropertyParser propertyParser)
    {
        this._propertyResolver = propertyResolver;
        this._propertyParser = propertyParser;
    }

    @Override
    public List<PropertyModel> parse(Type type) {
        List<PropertyModel> propertyModels = new ArrayList<>();

        List<PropertyItem> items = this._propertyResolver.resolve(type);
        for (PropertyItem item : items)
        {
            PropertyModel propertyModel = _propertyParser.parse(item);
            if (propertyModel != null) {
                propertyModels.add(propertyModel);
            }
        }
        return propertyModels;
    }
}
