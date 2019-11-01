package cn.willingxyz.restdoc.swagger3;

import cn.willingxyz.restdoc.swagger.common.SwaggerGeneratorConfig;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class Swagger3GeneratorConfig extends SwaggerGeneratorConfig {
    private List<IOpenAPIFilter> _openAPIFilters = new ArrayList<>();
}
