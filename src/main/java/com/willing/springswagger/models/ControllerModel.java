package com.willing.springswagger.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 代表一个controller
 */
@Data
public class ControllerModel {
//    private String _name;
//    private String _simpleName;
    // todo 简化类名
    private Class _controllerClass;
    private String _description;

    private List<PathModel> _controllerMethods = new ArrayList<>();
}
