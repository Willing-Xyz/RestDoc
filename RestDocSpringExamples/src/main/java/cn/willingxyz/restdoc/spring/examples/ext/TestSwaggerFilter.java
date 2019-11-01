package cn.willingxyz.restdoc.spring.examples.ext;

import cn.willingxyz.restdoc.swagger2.ISwaggerFilter;
import io.swagger.models.Swagger;

public class TestSwaggerFilter implements ISwaggerFilter {
    @Override
    public Swagger handle(Swagger swagger) {
        System.out.println("handle swagger");

        swagger.setHost("http://localhost:8084?info=add_by_extension");

        return swagger;
    }
}
