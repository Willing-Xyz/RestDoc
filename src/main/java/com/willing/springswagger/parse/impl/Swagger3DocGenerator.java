package com.willing.springswagger.parse.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import com.willing.springswagger.models.*;
import com.willing.springswagger.parse.IDocGenerator;
import com.willing.springswagger.parse.Swagger3Configuration;
import com.willing.springswagger.parse.utils.FormatUtils;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.var;

import java.util.*;

public class Swagger3DocGenerator implements IDocGenerator {

    private final Swagger3Configuration _configuration;

    public Swagger3DocGenerator(Swagger3Configuration configuration)
    {
        _configuration = configuration;
    }

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

        convertServers(rootModel, openApi);
        convertInfo(rootModel, openApi);
        convertTag(rootModel, openApi);
        convertPath(rootModel, openApi);

        return openApi;
    }

    private void convertServers(RootModel rootModel, OpenAPI openApi) {
        var servers = new ArrayList<Server>();
        for (var server : _configuration.getServers())
        {
            var serverInfo = new Server();
            serverInfo.setDescription(server.getDescription());
            serverInfo.setUrl(server.getUrl());

            servers.add(serverInfo);
        }
        openApi.setServers(servers);
    }

    private void convertInfo(RootModel rootModel, OpenAPI openApi) {
        var info = new Info();
        info.setDescription(_configuration.getDescription());
        info.setVersion(_configuration.getVersion());
        info.setTitle(_configuration.getTitle());
        openApi.setInfo(info);
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
                operation.setResponses(convertResponses(method, openApi));

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
                            pathItem.post(operation);
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

    /**
     * 转换RequestBody
     */
    private RequestBody convertRequestBody(ParameterModel parameterModel, OpenAPI openAPI) {
        var requestBody = new RequestBody();

        if (_configuration.getTypeInspector().isSimpleType(parameterModel.getParameterClass()))
        {
            // 如果是单个参数，直接嵌入
            var schema = new Schema();

            schema.setDescription(parameterModel.getDescription());
            schema.setType(_configuration.getTypeInspector().toSwaggerType(parameterModel.getParameterClass()));
            schema.setFormat(_configuration.getTypeInspector().toSwaggerFormat(parameterModel.getParameterClass()));

            if (parameterModel.getRequired() != null && parameterModel.getRequired()) {
                schema.setRequired(Arrays.asList(parameterModel.getName()));
            }

            var mediaType = new MediaType();
            mediaType.setSchema(schema);
            var content = new Content();
            content.addMediaType("application/json", mediaType);
            requestBody.setContent(content);
        }
        else {
            // 如果是复杂类型，引用Component
            var schema = new Schema();
            var mediaType = new MediaType();
            mediaType.setSchema(schema);
            var content = new Content();
            content.addMediaType("application/json", mediaType);
            requestBody.setContent(content);
            requestBody.setRequired(parameterModel.getRequired());
            requestBody.setDescription(parameterModel.getDescription());
//            requestBody.set$ref("#/components/schemas/" + putSchemaComponent(parameterModel, openAPI));

            schema.set$ref("#/components/schemas/" + putSchemaComponent(parameterModel.getParameterClass(), parameterModel.getChildren(), openAPI));
        }
        return requestBody;
    }

//    private String putSchemaComponent(List<> parameterModel, OpenAPI openAPI) {
//        var requestBody = new RequestBody();
//
//        requestBody.setDescription(parameterModel.getDescription());
//        requestBody.setRequired(parameterModel.getRequired());
//
//        var content = new Content();
//        var mediaType = new MediaType();
//        var schema = new Schema();
//        schema.set$ref("#/components/schemas/" + putSchemaComponent(parameterModel, openAPI));
//        mediaType.setSchema(schema);
//        content.addMediaType("application/json", mediaType);
//        requestBody.setContent(content);
////        requestBody.set$ref();
//
//        var paramClassName = parameterModel.getParameterClass().getCanonicalName();
//        if (openAPI.getComponents() == null)
//            openAPI.components(new Components());
//        if (!openAPI.getComponents().getRequestBodies().containsKey(paramClassName)) {
//            openAPI.getComponents().addRequestBodies(paramClassName, requestBody);
//        }
//        return paramClassName;
//    }

    private String putSchemaComponent(Class clazz, List<PropertyModel> children, OpenAPI openAPI) {
        var className = clazz.getCanonicalName();

        if (openAPI.getComponents() == null)
            openAPI.components(new Components());
        if (openAPI.getComponents().getSchemas() == null)
            openAPI.getComponents().schemas(new HashMap<>());
        if (openAPI.getComponents().getSchemas().containsKey(className))
        {
            return className;
        }
        var schema = generateSchemaComponent(clazz, children, openAPI);


        openAPI.getComponents().addSchemas(className, schema);

        return className;
    }

    private Schema generateSchemaComponent(Class clazz, List<PropertyModel> children, OpenAPI openAPI) {
        var schema = new Schema();
        var className = clazz.getCanonicalName();
        schema.setName(className);
        schema.setType(_configuration.getTypeInspector().toSwaggerType(clazz)); // todo

        var classDoc = RuntimeJavadoc.getJavadoc(className);
        schema.setDescription(FormatUtils.format(classDoc.getComment()));

        schema.setProperties(generateSchemaProperty(children, openAPI));
        return schema;
    }

    private Map<String, Schema> generateSchemaProperty(List<PropertyModel> propertyModels, OpenAPI openAPI) {
        var schemas = new HashMap<String, Schema>();

        for (var propertyModel : propertyModels) {
            var schema = new Schema();
            if (_configuration.getTypeInspector().isSimpleType(propertyModel.getPropertyClass())) {
//                schema.setTitle(parameterModel.getPropertyClass().getCanonicalName());
                schema.setDescription(propertyModel.getDescription());
                schema.setType(_configuration.getTypeInspector().toSwaggerType(propertyModel.getPropertyClass()));
                schema.setFormat(_configuration.getTypeInspector().toSwaggerFormat(propertyModel.getPropertyClass()));

                if (propertyModel.getRequired() != null && propertyModel.getRequired()) {
                    schema.setRequired(Arrays.asList(propertyModel.getName()));
                }
            }
            else
            {
                schema.set$ref("#/components/schemas/" +  putSchemaComponent(propertyModel.getPropertyClass(), propertyModel.getChildren(), openAPI));
//                schema.setProperties(generateSchemaProperty(propertyModel.getChildren()));
            }
            schemas.put(propertyModel.getName(), schema);
        }
        return schemas;
    }

//    private String putSchemaComponentByPropertyModel(PropertyModel propertyModel, OpenAPI openAPI) {
//        var className = propertyModel.getPropertyClass().getCanonicalName();
//
//        if (openAPI.getComponents() == null)
//            openAPI.components(new Components());
//        if (openAPI.getComponents().getSchemas() == null)
//            openAPI.getComponents().schemas(new HashMap<>());
//        if (openAPI.getComponents().getSchemas().containsKey(className))
//        {
//            return className;
//        }
//        var schema = generateSchemaComponentByPropertyModel(propertyModel, openAPI);
//
//
//        openAPI.getComponents().addSchemas(className, schema);
//
//        return className;
//    }
//
//    private Schema generateSchemaComponentByPropertyModel(PropertyModel propertyModel, OpenAPI openAPI) {
//    }

    private ApiResponses convertResponses(PathModel method, OpenAPI openAPI) {
        var response = new ApiResponses();
        for (var res : method.getResponse())
        {
            ApiResponse apiResponse = convertResponse(res, openAPI);
            response.addApiResponse(res.getStatusCode().value() + "", apiResponse);
        }
        return response;
    }

    private ApiResponse convertResponse(ResponseModel responseModel, OpenAPI openAPI) {
        var apiResponse = new ApiResponse();
        var returnModel = responseModel.getReturnModel();

//        if (_configuration.getTypeInspector().isSimpleType(returnModel.getReturnType()))
//        {
//            // 如果是单个参数，直接嵌入.
//            var schema = new Schema();
//
//            schema.setDescription(returnModel.getDescription());
//            schema.setType(_configuration.getTypeInspector().toSwaggerType(returnModel.getReturnType()));
//            schema.setFormat(_configuration.getTypeInspector().toSwaggerFormat(returnModel.getReturnType()));
//
//            var mediaType = new MediaType();
//            mediaType.setSchema(schema);
//            var content = new Content();
//            content.addMediaType("application/json", mediaType);
//            apiResponse.setContent(content);
//            apiResponse.setDescription(returnModel.getDescription());
//        }
//        else {
//            // 如果是复杂类型，引用Component
//            var schema = new Schema();
//            var mediaType = new MediaType();
//            mediaType.setSchema(schema);
//            var content = new Content();
//            content.addMediaType("application/json", mediaType);
//            apiResponse.setContent(content);
//            apiResponse.setDescription(returnModel.getDescription());
////            requestBody.set$ref("#/components/schemas/" + putSchemaComponent(parameterModel, openAPI));
//
//            schema.set$ref("#/components/schemas/" + putSchemaComponent(returnModel.getReturnType(), returnModel.getChildren(), openAPI));
//        }

        return apiResponse;
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
        return schema;
    }
}
