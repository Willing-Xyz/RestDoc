package cn.willingxyz.restdoc.swagger.common;

import cn.willingxyz.restdoc.core.config.RestDocParseConfig;
import cn.willingxyz.restdoc.core.parse.ITypeInspector;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SwaggerGeneratorConfig {

    protected final RestDocParseConfig _parseConfig;

    public SwaggerGeneratorConfig(RestDocParseConfig parseConfig)
    {
        _parseConfig = parseConfig;
    }

    private String _version;
    private String _description;
    private String _title;
    /**
     * 将JavaDoc的注释作为类的名称
     */
    private boolean _resolveJavaDocAsTypeName = false;

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
