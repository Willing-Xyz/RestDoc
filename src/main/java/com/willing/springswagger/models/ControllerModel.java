package com.willing.springswagger.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 代表一个controller
 */
@Data
public class ControllerModel {
    private String _name;
    private String _description;

    private List<PathModel> _controllerMethods = new ArrayList<>();
}
