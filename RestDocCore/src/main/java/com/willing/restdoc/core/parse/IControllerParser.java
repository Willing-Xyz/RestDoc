package com.willing.restdoc.core.parse;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.willing.restdoc.core.models.ControllerModel;

/**
 * 解析controller
 */
public interface IControllerParser {
    void parse(Class clazz, ClassJavadoc classJavadoc, ControllerModel controllerModel);
}
