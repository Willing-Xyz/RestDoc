package com.willing.springswagger.parse.impl;

import com.github.therapi.runtimejavadoc.Comment;
import com.willing.springswagger.models.ResponseModel;
import com.willing.springswagger.parse.IMethodReturnParser;
import com.willing.springswagger.parse.utils.FormatUtils;
import lombok.var;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Method;

public class MethodReturnParser implements IMethodReturnParser {

    @Override
    public ResponseModel parse(Method method, Comment returns, ResponseModel responseModel) {
        responseModel.setStatusCode(HttpStatus.OK);

        var returnValue = responseModel.getReturnModel();
        var returnType = method.getReturnType();

        returnValue.setDescription(FormatUtils.format(returns));
        returnValue.setReturnClass(returnType);






//        responseModel.setDescription(FormatUtils.format(returns));



        return responseModel;
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
