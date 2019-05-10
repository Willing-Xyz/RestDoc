package com.willing.restdoc.core.parse.impl;

import com.github.therapi.runtimejavadoc.ParamJavadoc;
import com.willing.restdoc.core.models.ParameterModel;
import com.willing.restdoc.core.parse.IMethodParameterParser;
import com.willing.restdoc.core.parse.RestDocParseConfig;
import com.willing.restdoc.core.parse.utils.TypeParseUtils;
import com.willing.restdoc.core.parse.utils.FormatUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Optional;

public abstract class AbstractMethodParameterParser implements IMethodParameterParser {
    protected final RestDocParseConfig _configuration;

    public AbstractMethodParameterParser(RestDocParseConfig configuration) {
        _configuration = configuration;
    }

    @Override
    public ParameterModel parse(Parameter parameter, ParamJavadoc paramJavadoc, ParameterModel parameterModel) {
        parameterModel.setName(getParameterName(parameter));

        if (paramJavadoc != null)
        {
            parameterModel.setDescription(FormatUtils.format(paramJavadoc.getComment()));
        }
        // 参数是Optional类型
        Type actualParamType = parameter.getParameterizedType();
        if (parameter.getType() == Optional.class)
        {
            if (parameter.getParameterizedType() instanceof ParameterizedTypeImpl)
            {
                actualParamType = ((ParameterizedTypeImpl)parameter.getParameterizedType()).getActualTypeArguments()[0];
            }
            else
            {
                throw new RuntimeException("why?");
            }
        }
        parameterModel.setParameterType(actualParamType);

        parameterModel.setLocation(getParameterLocation(parameter, actualParamType));
        parameterModel = parseInternal(parameter, actualParamType, parameterModel);
        parameterModel.setRequired(isRequired(parameter, actualParamType));

        if (actualParamType != parameter.getParameterizedType())
            parameterModel.setRequired(false);

        return parameterModel;
    }

    protected String getParameterName(Parameter parameter) {
        return parameter.getName();
        // todo
    }

    protected abstract ParameterModel.ParameterLocation getParameterLocation(Parameter parameter, Type actualParamType);

    protected abstract boolean isRequired(Parameter parameter, Type actualParamType);

    protected ParameterModel parseInternal(Parameter parameter, Type actualParamType, ParameterModel parameterModel)
    {
        boolean isArray = _configuration.getTypeInspector().isCollection(actualParamType);
        parameterModel.setArray(isArray);

        if (!isArray) {
            parameterModel.setChildren(TypeParseUtils.parseTypeProperty(_configuration, actualParamType));
        }
        else
        {
            parameterModel.setChildren(TypeParseUtils.parseTypeProperty(_configuration, _configuration.getTypeInspector().getCollectionComponentType(actualParamType)));
        }
        return parameterModel;
    }
}
