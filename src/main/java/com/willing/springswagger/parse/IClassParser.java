package com.willing.springswagger.parse;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.willing.springswagger.models.ControllerModel;

public interface IClassParser {
    ControllerModel parse(Class clazz, ClassJavadoc classJavadoc, ControllerModel controllerModel);
}
