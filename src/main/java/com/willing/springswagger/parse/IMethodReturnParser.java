package com.willing.springswagger.parse;

import com.github.therapi.runtimejavadoc.Comment;
import com.willing.springswagger.models.ResponseModel;

import java.lang.reflect.Method;

public interface IMethodReturnParser {
    ResponseModel parse(Method method, Comment returns, ResponseModel responseModel);

    /**
     * 是否创建一个新的Response，而不是在之前的Response上再加工
     * @return
     */
    boolean isNew();
}
