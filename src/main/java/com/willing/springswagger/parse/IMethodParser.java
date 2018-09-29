package com.willing.springswagger.parse;

import com.willing.springswagger.models.PathModel;

import java.lang.reflect.Method;

public interface IMethodParser {
    PathModel parse(Method method, PathModel pathModel);
}
