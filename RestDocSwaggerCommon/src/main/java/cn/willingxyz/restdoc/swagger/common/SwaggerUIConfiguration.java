package cn.willingxyz.restdoc.swagger.common;

import lombok.Data;

/**
 * 配置swagger-ui
 * 具体配置参考：https://github.com/swagger-api/swagger-ui/blob/master/docs/usage/configuration.md
 */
@Data
public class SwaggerUIConfiguration {
    private boolean _deepLinking = true;
    private boolean _displayOperationId = false;
    private int _defaultModelsExpandDepth = 0;
    private int _defaultModelExpandDepth = 100;
    private String _defaultModelRendering = "example";
    private boolean _displayRequestDuration = true;

    private String _docExpansion = "none";
    private int _maxDisplayedTags;
    private boolean _showExtensions;
    private boolean _showCommonExtensions;

    private String _layout = "StandaloneLayout";
}
