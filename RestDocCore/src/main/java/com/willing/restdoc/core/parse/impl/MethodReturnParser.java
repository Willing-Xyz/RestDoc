package com.willing.restdoc.core.parse.impl;

import com.github.therapi.runtimejavadoc.Comment;
import com.willing.restdoc.core.models.ResponseModel;
import com.willing.restdoc.core.parse.DocParseConfiguration;
import com.willing.restdoc.core.parse.IMethodReturnParser;
import com.willing.restdoc.core.parse.utils.ClassUtils;
import com.willing.restdoc.core.parse.utils.FormatUtils;
import com.willing.restdoc.core.parse.utils.ReflectUtils;
import lombok.var;

import java.lang.reflect.Method;

public class MethodReturnParser implements IMethodReturnParser {

    private final DocParseConfiguration _configuration;

    public MethodReturnParser(DocParseConfiguration configuration)
    {
        _configuration = configuration;
    }

    @Override
    public ResponseModel parse(Method method, Comment returns, ResponseModel responseModel) {
        responseModel.setStatusCode(200);

        var returnModel = responseModel.getReturnModel();

        returnModel.setDescription(FormatUtils.format(returns));
        returnModel.setReturnType(method.getGenericReturnType());

        boolean isArray = ReflectUtils.isArray(method.getGenericReturnType());
        returnModel.setArray(isArray);

        // todo required
        if (!isArray) {
            returnModel.setChildren(ClassUtils.parseTypeProperty(_configuration, method.getGenericReturnType()));
        }
        else
        {
            returnModel.setChildren(ClassUtils.parseTypeProperty(_configuration, ReflectUtils.getArrayComponentType(method.getGenericReturnType())));
        }
        return responseModel;
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
