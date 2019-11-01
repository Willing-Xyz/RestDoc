package cn.willingxyz.restdoc.swagger3;

import io.swagger.v3.oas.models.OpenAPI;

@FunctionalInterface
public interface IOpenAPIFilter {
    OpenAPI handle(OpenAPI openAPI);
}
