package cn.willingxyz.restdoc.swagger.common;

import cn.willingxyz.restdoc.core.parse.ITypeInspector;
import cn.willingxyz.restdoc.core.parse.ITypeNameParser;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class SwaggerGeneratorConfig {

    private String _version;
    private String _description;
    private String _title;
    private List<ServerInfo> _servers = new ArrayList<>();
    private ISwaggerTypeInspector _swaggerTypeInspector;
    private ITypeInspector _typeInspector;
    private ITypeNameParser _typeNameParser;

    @Data
    @Builder
    public static class ServerInfo
    {
        private String _description;
        private String _url;
    }
}
