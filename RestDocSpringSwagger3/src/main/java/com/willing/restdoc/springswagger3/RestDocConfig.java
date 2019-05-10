package com.willing.restdoc.springswagger3;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class RestDocConfig {
    private List<String> _packages = new ArrayList<>();
    private String _fieldPrefix;

    private String _apiTitle;
    private String _apiDescription;
    private String _apiVersion;
}
