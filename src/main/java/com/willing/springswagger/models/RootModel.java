package com.willing.springswagger.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * root
 */
@Data
public class RootModel {
    private List<ControllerModel> _controllers = new ArrayList<>();
}
