package cn.willingxyz.restdoc.spring.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.QueryParameter;

import java.util.HashMap;

public class Swagger2App {
    public static void main(String[] args) throws JsonProcessingException {
        Swagger swagger = new Swagger();

        swagger.setParameters(new HashMap<>());

        QueryParameter queryParam = new QueryParameter();
        queryParam.setName("param1");
        queryParam.setPattern("test");
        queryParam.setMultipleOf(12);
        queryParam.setUniqueItems(true);
        swagger.getParameters().put("queryParam", queryParam);










        ObjectMapper objectMapper = new ObjectMapper();
        String swaggerJson = objectMapper.writeValueAsString(swagger);


        System.out.println();
        System.out.println(swaggerJson);
        System.out.println();
    }
}
