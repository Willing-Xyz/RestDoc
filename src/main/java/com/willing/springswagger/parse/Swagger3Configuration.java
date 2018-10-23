package com.willing.springswagger.parse;

import com.willing.springswagger.parse.impl.ComposeSwaggerTypeInspector;
import com.willing.springswagger.parse.impl.PrimitiveSwaggerTypeInspector;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class Swagger3Configuration {
    private String _version;
    private String _description;
    private String _title;
    private List<ServerInfo> _servers = new ArrayList<>();
    private ISwaggerTypeInspector _typeInspector = new ComposeSwaggerTypeInspector(Arrays.asList(new PrimitiveSwaggerTypeInspector()));

    @Data
    @Builder
    public static class ServerInfo
    {
        private String _description;
        private String _url;
    }
}
