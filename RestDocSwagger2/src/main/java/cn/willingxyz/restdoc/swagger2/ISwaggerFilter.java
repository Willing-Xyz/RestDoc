package cn.willingxyz.restdoc.swagger2;

import io.swagger.models.Swagger;

public interface ISwaggerFilter {
    Swagger handle(Swagger swagger);
}
