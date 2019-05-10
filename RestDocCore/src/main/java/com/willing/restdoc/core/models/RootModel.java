package com.willing.restdoc.core.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * root
 */
@Data
public class RootModel {
    /**
     * Controller集合
     */
    private List<ControllerModel> _controllers = new ArrayList<>();
}
