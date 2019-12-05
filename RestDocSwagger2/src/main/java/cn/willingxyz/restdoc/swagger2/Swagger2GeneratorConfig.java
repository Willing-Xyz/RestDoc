package cn.willingxyz.restdoc.swagger2;

import cn.willingxyz.restdoc.core.config.RestDocParseConfig;
import cn.willingxyz.restdoc.core.utils.ServiceLoaders;
import cn.willingxyz.restdoc.swagger.common.SwaggerGeneratorConfig;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Swagger2GeneratorConfig extends SwaggerGeneratorConfig {
    private List<ISwaggerFilter> _swaggerFilters;

    public Swagger2GeneratorConfig(RestDocParseConfig parseConfig) {
        super(parseConfig);

        _swaggerFilters = ServiceLoaders.loadServices(ISwaggerFilter.class, parseConfig);
    }
}
