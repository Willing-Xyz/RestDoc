package cn.willingxyz.restdoc.swagger2;

import cn.willingxyz.restdoc.swagger.common.SwaggerGeneratorConfig;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Swagger2GeneratorConfig extends SwaggerGeneratorConfig {
    private List<ISwaggerFilter> _swaggerFilters = new ArrayList<>();
}
