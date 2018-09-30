package com.willing.springswagger.parse.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.willing.springswagger.models.ParameterModel;
import com.willing.springswagger.models.ParameterType;
import com.willing.springswagger.models.PathModel;
import com.willing.springswagger.models.RootModel;
import com.willing.springswagger.parse.IDocGenerator;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.var;

import java.util.*;

public class Swagger3DocGenerator implements IDocGenerator {

    @Override
    public String generate(RootModel rootModel) {
        var openApi = generateOpenApi(rootModel);

        var objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            var swaggerJson = objectMapper.writeValueAsString(openApi);
            return swaggerJson;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化错误");
        }
    }

    private OpenAPI generateOpenApi(RootModel rootModel) {
        var openApi = new OpenAPI();

        convertTag(rootModel, openApi);
        convertPath(rootModel, openApi);

        return openApi;
    }

    private void convertTag(RootModel rootModel, OpenAPI openApi) {
        for (var controller : rootModel.getControllers())
        {
            var tag = new Tag();
            tag.setName(controller.getSimpleName());
            tag.setDescription(controller.getDescription());
            openApi.addTagsItem(tag);
        }
    }

    private void convertPath(RootModel rootModel, OpenAPI openApi) {
        for (var controller : rootModel.getControllers())
        {
            for (var method : controller.getControllerMethods())
            {
                var pathItem = new PathItem();
                pathItem.setDescription(method.getDescription());
                pathItem.setSummary(method.getDescription()); // todo

                var operation = new Operation();
                operation.addTagsItem(controller.getSimpleName());
                operation.setSummary(method.getDescription()); // todo
                operation.setDescription(method.getDescription());
                operation.setDeprecated(method.getDeprecated());

                for (var param : method.getParameters()) {
                    if (param.getLocation() == ParameterModel.ParameterLocation.QUERY) {
                        operation.addParametersItem(convertParameter(param));
                    }
                    else if (param.getLocation() == ParameterModel.ParameterLocation.BODY)
                    {
                        operation.setRequestBody(convertRequestBody(param, openApi));
                    }
                }
                operation.setResponses(convertResponse(method));

                for (var httpMethod : method.getHttpMethods())
                {
                    switch (httpMethod) {
                        case GET:
                            pathItem.get(operation);
                            break;
                        case PUT:
                            pathItem.put(operation);
                            break;
                        case POST:
                            pathItem.put(operation);
                            break;
                        case DELETE:
                            pathItem.delete(operation);
                            break;
                        case HEAD:
                            pathItem.head(operation);
                            break;
                        case PATCH:
                            pathItem.patch(operation);
                            break;
                        case TRACE:
                            pathItem.patch(operation);
                            break;
                        case OPTIONS:
                            pathItem.options(operation);
                            break;
                    }
                }
                for (var path : method.getPaths()) {
                    openApi.path(path, pathItem);
                }
            }
        }
    }

    private RequestBody convertRequestBody(ParameterModel parameterModel, OpenAPI openAPI) {
        var requestBody = new RequestBody();
//        requestBody.setDescription(parameterModel.getDescription());
        requestBody.setRequired(parameterModel.getRequired());
        requestBody.set$ref("#/components/requestBodies/" + putRequestBodyComponent(parameterModel, openAPI).getKey());
        return requestBody;
    }

    private Map.Entry<String, RequestBody> putRequestBodyComponent(ParameterModel parameterModel, OpenAPI openAPI) {
        var requestBody = new RequestBody();

        requestBody.setDescription(parameterModel.getDescription());
        requestBody.set$ref("#/components/schemas/" + putSchemaComponent(parameterModel, openAPI));

        var paramClassName = parameterModel.getParameterClass().getCanonicalName();
        if (openAPI.getComponents() == null)
            openAPI.components(new Components());
        openAPI.getComponents().addRequestBodies(paramClassName, requestBody);
        return new AbstractMap.SimpleEntry<>(paramClassName, requestBody);
    }

    private String putSchemaComponent(ParameterModel parameterModel, OpenAPI openAPI) {
        var className = parameterModel.getParameterClass().getCanonicalName();

        var schema = generateSchemaComponent(parameterModel);

        if (openAPI.getComponents() == null)
            openAPI.components(new Components());
        openAPI.getComponents().addSchemas(className, schema);

        return className;
    }

    private Schema generateSchemaComponent(ParameterModel parameterModel) {
        var schema = new Schema();
        var className = parameterModel.getParameterClass().getCanonicalName();
        schema.setName(className);
        schema.setType("object"); // todo
        schema.setDescription(parameterModel.getDescription());

        var properties = new HashMap<String, Schema>();

        for (var sub : parameterModel.getChildren())
        {
            properties.put(sub.getParameterClass().getCanonicalName(), generateSchemaComponent(parameterModel));
        }

        schema.setProperties(properties);
        return schema;
    }
    private ApiResponses convertResponse(PathModel method) {
        var response = new ApiResponses();
        for (var res : method.getResponse())
        {
            var apiResponse = new ApiResponse();
            apiResponse.setDescription(res.getReturnModel().getDescription());
            response.addApiResponse(res.getStatusCode().value() + "", apiResponse);
        }
        return response;
    }

    private Parameter convertParameter(ParameterModel paramModel) {
        var parameter = new Parameter();
        parameter.setName(paramModel.getName());
        parameter.setDescription(paramModel.getDescription());
        switch (paramModel.getLocation())
        {
            case BODY:
                parameter.setIn("body");
                break;case QUERY: parameter.setIn("query");
                break;
        }
        parameter.setSchema(convertSchema(paramModel));
        return parameter;
    }

    private Schema convertSchema(ParameterModel paramModel) {
        var schema = new Schema();
        schema.setType(convertSchemaType(paramModel.getType()));
        return schema;
    }

    private String convertSchemaType(ParameterType type) {
        switch (type)
        {
            case INTEGER:
                return "integer";
            case BOOLEAN:
                return "boolean";
            case STRING:
                return "string";
                // todo
                default:
                    return "object";
        }
    }
}
