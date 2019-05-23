package cn.willingxyz.restdoc.core.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 代表一个controller
 */
@Data
public class ControllerModel {
    private Class _controllerClass;
    private String _description;

    private List<PathModel> _controllerMethods = new ArrayList<>();
}
