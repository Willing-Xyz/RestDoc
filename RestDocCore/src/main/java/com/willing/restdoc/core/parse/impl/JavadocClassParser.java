package com.willing.restdoc.core.parse.impl;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.willing.restdoc.core.models.ControllerModel;
import com.willing.restdoc.core.parse.utils.FormatUtils;
import com.willing.restdoc.core.parse.IClassParser;

public class JavadocClassParser implements IClassParser {

    @Override
    public ControllerModel parse(Class clazz, ClassJavadoc classDoc, ControllerModel controllerModel) {

        if (classDoc != null) {
            controllerModel.setControllerClass(clazz);
            controllerModel.setDescription(FormatUtils.format(classDoc.getComment()));
        }

        return controllerModel;
    }
}
