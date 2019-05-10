package com.willing.restdoc.core.parse.impl;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.willing.restdoc.core.models.ControllerModel;
import com.willing.restdoc.core.parse.utils.FormatUtils;
import com.willing.restdoc.core.parse.IControllerParser;

public class JavadocControllerParser implements IControllerParser {

    @Override
    public void parse(Class clazz, ClassJavadoc classDoc, ControllerModel controllerModel) {
        controllerModel.setControllerClass(clazz);
        if (classDoc != null && classDoc.isPresent()) {
            controllerModel.setDescription(FormatUtils.format(classDoc.getComment()));
        }
    }
}
