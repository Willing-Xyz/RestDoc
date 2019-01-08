package com.willing.restdoc.core.parse;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.willing.restdoc.core.models.ControllerModel;

public interface IClassParser {
    ControllerModel parse(Class clazz, ClassJavadoc classJavadoc, ControllerModel controllerModel);
}
