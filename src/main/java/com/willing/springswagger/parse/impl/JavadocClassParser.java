package com.willing.springswagger.parse.impl;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.willing.springswagger.models.ControllerModel;
import com.willing.springswagger.parse.utils.FormatUtils;
import com.willing.springswagger.parse.IClassParser;
import lombok.var;

public class JavadocClassParser implements IClassParser {

    @Override
    public ControllerModel parse(Class clazz, ClassJavadoc classDoc, ControllerModel controllerModel) {
        var className = clazz.getCanonicalName();

        if (classDoc != null) {
            controllerModel.setName(className);
            controllerModel.setSimpleName(clazz.getSimpleName());
            controllerModel.setDescription(FormatUtils.format(classDoc.getComment()));
        }

        return controllerModel;
    }
}
