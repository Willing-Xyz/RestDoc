package com.willing.springswagger.parse;

import com.willing.springswagger.models.ControllerModel;

public interface IClassParser {
    ControllerModel parse(Class clazz, ControllerModel controllerModel);
}
