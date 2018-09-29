package com.willing.springswagger.parse.impl;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.CommentFormatter;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import com.willing.springswagger.models.ControllerModel;
import com.willing.springswagger.parse.IClassParser;
import lombok.var;

import java.util.Optional;

public class JavadocClassParser implements IClassParser {
    private final CommentFormatter _formatter = new CommentFormatter();

    @Override
    public ControllerModel parse(Class clazz, ClassJavadoc classDoc, ControllerModel controllerModel) {
        var className = clazz.getCanonicalName();

        if (classDoc != null) {
            controllerModel.setName(className);
            controllerModel.setDescription(_formatter.format(classDoc.getComment()));
        }

        return controllerModel;
    }
}
