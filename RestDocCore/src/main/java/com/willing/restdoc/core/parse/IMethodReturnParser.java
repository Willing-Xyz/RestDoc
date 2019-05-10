package com.willing.restdoc.core.parse;

import com.github.therapi.runtimejavadoc.Comment;
import com.willing.restdoc.core.models.ResponseModel;

import java.lang.reflect.Method;

/**
 * 解析返回类型
 */
public interface IMethodReturnParser {

    ResponseModel parse(Method method, Comment returns, ResponseModel responseModel);

    /**
     * 是否创建一个新的Response，而不是在之前的Response上再加工
     */
    boolean isNew();
}
