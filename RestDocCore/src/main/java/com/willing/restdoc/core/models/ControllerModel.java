package com.willing.restdoc.core.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 代表一个controller
 */
@Data
public class ControllerModel {
    // todo 简化类名
    private Class _controllerClass;
    private String _description;

    private List<PathModel> _controllerMethods = new ArrayList<>();
}
