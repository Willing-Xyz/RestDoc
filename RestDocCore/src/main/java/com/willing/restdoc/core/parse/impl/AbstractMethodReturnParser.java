package com.willing.restdoc.core.parse.impl;

import com.github.therapi.runtimejavadoc.Comment;
import com.willing.restdoc.core.models.ResponseModel;
import com.willing.restdoc.core.parse.RestDocParseConfig;
import com.willing.restdoc.core.parse.IMethodReturnParser;
import com.willing.restdoc.core.parse.utils.TypeParseUtils;
import com.willing.restdoc.core.parse.utils.FormatUtils;
import com.willing.restdoc.core.parse.utils.ReflectUtils;
import lombok.var;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;

public abstract class AbstractMethodReturnParser implements IMethodReturnParser {

    private final RestDocParseConfig _configuration;

    public AbstractMethodReturnParser(RestDocParseConfig configuration)
    {
        _configuration = configuration;
    }

    @Override
    public ResponseModel parse(Method method, Comment returns, ResponseModel responseModel) {
        responseModel.setStatusCode(parseStatusCode(method));

        var returnModel = responseModel.getReturnModel();

        returnModel.setDescription(FormatUtils.format(returns));

        Type actualType = method.getGenericReturnType();
        if (actualType == Optional.class)
        {
            if (method.getGenericReturnType() instanceof ParameterizedTypeImpl)
            {
                actualType = ((ParameterizedTypeImpl)method.getGenericReturnType()).getActualTypeArguments()[0];
            }
            else
            {
                throw new RuntimeException("why?");
            }
        }
        returnModel.setReturnType(actualType);

        boolean isArray = _configuration.getTypeInspector().isCollection(actualType);
        returnModel.setArray(isArray);

        if (!isArray) {
            returnModel.setChildren(TypeParseUtils.parseTypeProperty(_configuration, actualType));
        }
        else
        {
            returnModel.setChildren(TypeParseUtils.parseTypeProperty(_configuration, _configuration.getTypeInspector().getCollectionComponentType(actualType)));
        }
        parseInternal(method, actualType, responseModel);
        return responseModel;
    }

    protected ResponseModel parseInternal(Method method, Type actualType, ResponseModel responseModel)
    {
        return responseModel;
    }

    protected abstract int parseStatusCode(Method method);

    @Override
    public boolean isNew() {
        return false;
    }
}
