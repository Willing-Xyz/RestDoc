package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.parse.IPropertyParser;
import cn.willingxyz.restdoc.core.parse.IPropertyResolver;
import cn.willingxyz.restdoc.core.parse.RestDocParseConfig;
import cn.willingxyz.restdoc.core.parse.utils.FormatUtils;
import cn.willingxyz.restdoc.core.parse.utils.GraphChecker;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import lombok.var;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class PropertyParser implements IPropertyParser {
    private final RestDocParseConfig _config;
    private final IPropertyResolver _propertyResolver;

    public PropertyParser(RestDocParseConfig config, IPropertyResolver propertyResolver)
    {
        this._config = config;
        this._propertyResolver = propertyResolver;
    }

    @Override
    public PropertyModel parse(PropertyItem item) {
        GraphChecker<Type> graphChecker = new GraphChecker<>();
        return parseProperty(this._config, null,graphChecker, item);
    }

    private PropertyModel parseProperty(RestDocParseConfig configuration, Type fromType,  GraphChecker<Type> map, PropertyItem propertyItem) {
        var propertyModel = new PropertyModel();
        propertyModel.setName(propertyItem.getPropertyName());
        propertyModel.setPropertyType(propertyItem.getPropertyType());
        setPropertyDescription(propertyItem, propertyItem, propertyModel);

        if (configuration.getTypeInspector().isCollection(propertyItem.getPropertyType()))
        {
            propertyModel.setArray(true);
            propertyModel.setChildren(parseTypeProperty(configuration, fromType, configuration.getTypeInspector().getCollectionComponentType(propertyItem.getPropertyType()), map));
        }
        else {
            propertyModel.setChildren(parseTypeProperty(configuration, fromType, propertyItem.getPropertyType(), map));
        }
        return propertyModel;
    }

    private void setPropertyDescription(PropertyItem item, PropertyItem propertyItem, PropertyModel propertyModel) {
        var declaringClass = item.getDeclaringClass();
        var classdoc = RuntimeJavadoc.getJavadoc(declaringClass);
        if (propertyItem.getField() != null) {
            var fieldDoc = classdoc.getFields().stream().filter(o -> o.getName().equals(propertyItem.getField().getName())).findFirst();
            if (fieldDoc.isPresent()) {
                propertyModel.setDescription(FormatUtils.format(fieldDoc.get().getComment()));
            }
        }
        if (propertyModel.getDescription() == null || propertyModel.getDescription().length() == 0)
        {
            if (item.getGetMethod() != null) {
                var getMethodDoc = classdoc.getMethods().stream().filter(o -> o.getName().equals(item.getGetMethod().getName())).findFirst();
                if (getMethodDoc.isPresent())
                    propertyModel.setDescription(FormatUtils.format(getMethodDoc.get().getComment()));
            }

        }
        if (propertyModel.getDescription() == null || propertyModel.getDescription().length() == 0)
        {
            if (item.getSetMethod() != null) {
                var setMethodDoc = classdoc.getMethods().stream().filter(o -> o.getName().equals(item.getSetMethod().getName())).findFirst();
                if (setMethodDoc.isPresent())
                    propertyModel.setDescription(FormatUtils.format(setMethodDoc.get().getComment()));
            }
        }
    }

    private List<PropertyModel> parseTypeProperty(RestDocParseConfig configuration, Type fromType, Type toType, GraphChecker<Type> graph)
    {
        var propertyModels = new ArrayList<PropertyModel>();

        if (graph.add(fromType, toType))
            return propertyModels;

        // 如果是集合类型，解析组件类型
        if (configuration.getTypeInspector().isCollection(toType))
        {
            var propertyModel = new PropertyModel();

            propertyModel.setName("array");
            propertyModel.setArray(true);
            propertyModel.setPropertyType(toType);

            var children = parseTypeProperty(configuration, toType, configuration.getTypeInspector().getCollectionComponentType(toType), graph);
            propertyModel.setChildren(children);
            propertyModels.add(propertyModel);
        }
        else if (toType instanceof Map) // map作为object看待
        {
            return parseClassProperty(configuration, Object.class, graph);
        }
        else if (toType instanceof ParameterizedType) // 参数化类型
        {
            if (Map.class.isAssignableFrom((Class<?>) ((ParameterizedType) toType).getRawType()))
                return parseClassProperty(configuration, Object.class, graph);
            return parseParameterizedTypeProperty(configuration, (ParameterizedType) toType, graph);
        }
        else if (toType instanceof Class)
        {
            return parseClassProperty(configuration, (Class)toType, graph);
        }

        return propertyModels;
    }

    private  List<PropertyModel> parseParameterizedTypeProperty(RestDocParseConfig configuration, ParameterizedType parameterizedType, GraphChecker<Type> map) {
        List<PropertyModel> propertyModels = new ArrayList<>();
        for (var propertyItem : this._propertyResolver.resolve(parameterizedType))
        {
            PropertyModel propertyModel = parseProperty(configuration, parameterizedType, map, propertyItem);
            propertyModels.add(propertyModel);
        }
        return propertyModels;
    }


    private List<PropertyModel> parseClassProperty(RestDocParseConfig configuration, Class<?> clazz, GraphChecker<Type> map) {
        var propertyModels = new ArrayList<PropertyModel>();

        for (var item : this._propertyResolver.resolve(clazz))
        {
            propertyModels.add(parseProperty(configuration, clazz, map, item));
        }

        return propertyModels;
    }
}
