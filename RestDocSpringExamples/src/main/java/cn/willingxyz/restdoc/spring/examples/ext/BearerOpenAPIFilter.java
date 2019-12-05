package cn.willingxyz.restdoc.spring.examples.ext;

import cn.willingxyz.restdoc.swagger3.IOpenAPIFilter;
import com.google.auto.service.AutoService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.var;

@AutoService(IOpenAPIFilter.class)
public class BearerOpenAPIFilter implements IOpenAPIFilter {
    @Override
    public OpenAPI handle(OpenAPI openApi) {
        var components = openApi.getComponents();
        // security 添加 token
        var scheme = new SecurityScheme();
        scheme.setType(SecurityScheme.Type.HTTP);
        scheme.setScheme("bearer");
        scheme.setBearerFormat("JWT");
        components.addSecuritySchemes("bearerAuth", scheme);
        // path 添加 token
        var paths = openApi.getPaths();
        var securityRequirement = new SecurityRequirement().addList("bearerAuth");
        paths.forEach((s, pathItem) -> {
            handelPathItem(pathItem.getGet(), securityRequirement);
            handelPathItem(pathItem.getPost(), securityRequirement);
            handelPathItem(pathItem.getPut(), securityRequirement);
            handelPathItem(pathItem.getDelete(), securityRequirement);
            handelPathItem(pathItem.getPatch(), securityRequirement);
            handelPathItem(pathItem.getHead(), securityRequirement);
            handelPathItem(pathItem.getOptions(), securityRequirement);
            handelPathItem(pathItem.getTrace(), securityRequirement);
        });
        return openApi;
    }


    private void handelPathItem(Operation operation, SecurityRequirement securityRequirement) {
        if (operation != null) {
            operation.addSecurityItem(securityRequirement);
        }
    }
}
