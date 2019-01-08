package com.willing.restdoc.swagger3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import com.willing.restdoc.core.models.*;
import com.willing.restdoc.core.parse.IDocGenerator;
import com.willing.restdoc.core.parse.utils.FormatUtils;
import com.willing.restdoc.core.parse.utils.ReflectUtils;
import com.willing.restdoc.core.parse.utils.UrlEncodeUtils;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
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

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class Swagger3DocGenerator implements IDocGenerator {

    public Swagger3Configuration _configuration;

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
            tag.setName(controller.getControllerClass().getCanonicalName());
            tag.setDescription(controller.getDescription());
            openApi.addTagsItem(tag);
        }
    }

    private void convertPath(RootModel rootModel, OpenAPI openApi) {
        for (var controller : rootModel.getControllers())
        {
            for (var method : controller.getControllerMethods())
            {
                for (var mapping : method.getMappings())
                {
                    var pathItem = new PathItem();
                    pathItem.setDescription(method.getDescription());
                    pathItem.setSummary(method.getDescription()); // todo

                    var operation = new Operation();
                    operation.addTagsItem(controller.getControllerClass().getCanonicalName());
                    operation.setSummary(method.getDescription()); // todo
                    operation.setDescription(method.getDescription());
                    operation.setDeprecated(method.getDeprecated());

                    for (var param : method.getParameters()) {
                        if (param.getLocation() == ParameterModel.ParameterLocation.QUERY) {
                            convertQueryString(param).stream().forEach(o -> operation.addParametersItem(o));
                        }
                        else if (param.getLocation() == ParameterModel.ParameterLocation.BODY)
                        {
                            operation.setRequestBody(convertRequestBody(param, openApi));
                        }
                        else if (param.getLocation() == ParameterModel.ParameterLocation.PATH)
                        {
                            operation.addParametersItem(convertPathParameter(param));
                        }
                    }
                    operation.setResponses(convertResponses(method, openApi));

                    for (var httpMethod : mapping.getHttpMethods())
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
                    for (var path : mapping.getPaths()) {
                        openApi.path(path, pathItem);
                    }
                }
            }
        }
    }


    /**
     * 转换RequestBody
     */
    private RequestBody convertRequestBody(ParameterModel parameterModel, OpenAPI openAPI) {
        var requestBody = new RequestBody();

        if (parameterModel.isArray()) {
            var arraySchema = new ArraySchema();
            var itemSchema = new Schema();
            itemSchema.set$ref("#/components/schemas/" + putSchemaComponent(parameterModel.getParameterType(), parameterModel.getChildren(), openAPI));
            arraySchema.setItems(itemSchema);

            var mediaType = new MediaType();
            mediaType.setSchema(arraySchema);
            var content = new Content();
            content.addMediaType("application/json", mediaType);
            requestBody.setContent(content);
            requestBody.setRequired(parameterModel.getRequired());
            requestBody.setDescription(parameterModel.getDescription());
        }
        else if (parameterModel.getChildren() == null || parameterModel.getChildren().size() == 0)
        {
            // 如果是单个参数，直接嵌入
            Schema schema = null;
            if (ReflectUtils.isEnum(parameterModel.getParameterType()))
            {
                var clazz = (Class)parameterModel.getParameterType();
                    var arraySchema = new ArraySchema();
                    var enumDoc = RuntimeJavadoc.getJavadoc(clazz);
                    arraySchema.setDescription(FormatUtils.format(enumDoc.getComment()));
                    var itemSchema = new Schema<String>();
                    itemSchema.setType("string");
                    var enums = Arrays.stream(clazz.getEnumConstants()).map(o -> o.toString()).collect(Collectors.toList());
                    itemSchema.setEnum(enums);
                    arraySchema.setItems(itemSchema);
                    if (enums.size() > 0)
                    {
                        itemSchema.setDefault(enums.get(0));
                    }
                    schema = arraySchema;
                    schema.setDescription(parameterModel.getDescription());
                    if (parameterModel.getRequired() != null && parameterModel.getRequired()) {
                    schema.setRequired(Arrays.asList(parameterModel.getName()));
                }
            }
            else {
                schema = new Schema();

                schema.setDescription(parameterModel.getDescription());
                schema.setType(_configuration.getTypeInspector().toSwaggerType(parameterModel.getParameterType()));
                schema.setFormat(_configuration.getTypeInspector().toSwaggerFormat(parameterModel.getParameterType()));

                if (parameterModel.getRequired() != null && parameterModel.getRequired()) {
                    schema.setRequired(Arrays.asList(parameterModel.getName()));
                }
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

            schema.set$ref("#/components/schemas/" + putSchemaComponent(parameterModel.getParameterType(), parameterModel.getChildren(), openAPI));
        }
        return requestBody;
    }


    private String putSchemaComponent(Type type, List<PropertyModel> children, OpenAPI openAPI) {
        var className = type.getTypeName();

        if (openAPI.getComponents() == null)
            openAPI.components(new Components());
        if (openAPI.getComponents().getSchemas() == null)
            openAPI.getComponents().schemas(new HashMap<>());
        if (openAPI.getComponents().getSchemas().containsKey(className))
        {
            return UrlEncodeUtils.encode(className);
        }
        var schema = generateSchemaComponent(type, children, openAPI);


        openAPI.getComponents().addSchemas(className, schema);

        return UrlEncodeUtils.encode(className);
    }

    private Schema generateSchemaComponent(Type type, List<PropertyModel> children, OpenAPI openAPI) {

        if (children.size() == 1 && children.get(0).isArray())
        {
            var arraySchema = new ArraySchema();
            arraySchema.setItems(generateSchemaComponent(children.get(0).getPropertyType(), children.get(0).getChildren(), openAPI));
            return arraySchema;
        }
        var schema = new Schema();
        var className = type.getTypeName();
        schema.setName(className);
        schema.setType(_configuration.getTypeInspector().toSwaggerType(type)); // todo

        var classDoc = RuntimeJavadoc.getJavadoc(className);
        schema.setDescription(FormatUtils.format(classDoc.getComment()));

        schema.setProperties(generateSchemaProperty(children, openAPI));
        return schema;
    }

    private Map<String, Schema> generateSchemaProperty(List<PropertyModel> propertyModels, OpenAPI openAPI) {
        var schemas = new HashMap<String, Schema>();

        for (var propertyModel : propertyModels) {
            var schema = new Schema();
            if (propertyModel.getChildren() != null && propertyModel.getChildren().size() == 0) {
//                schema.setTitle(parameterModel.getPropertyType().getCanonicalName());
                schema.setDescription(propertyModel.getDescription());

                if (ReflectUtils.isEnum(propertyModel.getPropertyType())) {
                    var clazz = (Class)propertyModel.getPropertyType();
                    schema = generateEnumSchema(clazz);
                }
                else {
                    schema.setType(_configuration.getTypeInspector().toSwaggerType(propertyModel.getPropertyType()));
                    schema.setFormat(_configuration.getTypeInspector().toSwaggerFormat(propertyModel.getPropertyType()));
                }

                if (propertyModel.getRequired() != null && propertyModel.getRequired()) {
                    schema.setRequired(Arrays.asList(propertyModel.getName()));
                }
            }
            else
            {
                schema.set$ref("#/components/schemas/" + putSchemaComponent(propertyModel.getPropertyType(), propertyModel.getChildren(), openAPI));
//                schema.setProperties(generateSchemaProperty(propertyModel.getChildren()));
            }
            schemas.put(propertyModel.getName(), schema);
        }
        return schemas;
    }

    private ArraySchema generateEnumSchema(Class clazz) {
        var arraySchema = new ArraySchema();
        var enumDoc = RuntimeJavadoc.getJavadoc(clazz);
        arraySchema.setDescription(FormatUtils.format(enumDoc.getComment()));
        var enums = Arrays.stream(clazz.getEnumConstants()).map(o -> o.toString()).collect(Collectors.toList());
        var itemSchema = new Schema();
        itemSchema.setType("string"); // todo 如何决定是string还是int
        itemSchema.setEnum(enums);
        if (enums.size() > 0) {
            itemSchema.setDefault(enums.get(0));
        }
        arraySchema.setItems(itemSchema);
        return arraySchema;
    }

    private ApiResponses convertResponses(PathModel method, OpenAPI openAPI) {
        var response = new ApiResponses();
        for (var res : method.getResponse())
        {
            ApiResponse apiResponse = convertResponse(res, openAPI);
            response.addApiResponse(res.getStatusCode() + "", apiResponse);
        }
        return response;
    }

    private ApiResponse convertResponse(ResponseModel responseModel, OpenAPI openAPI) {
        var apiResponse = new ApiResponse();
        var returnModel = responseModel.getReturnModel();

        if (returnModel.isArray()) {
            var arraySchema = new ArraySchema();
            var itemSchema = new Schema();
            itemSchema.set$ref("#/components/schemas/" + putSchemaComponent(returnModel.getReturnType(), returnModel.getChildren().get(0).getChildren(), openAPI));
            arraySchema.setItems(itemSchema);

            var mediaType = new MediaType();
            mediaType.setSchema(arraySchema);
            var content = new Content();
            content.addMediaType("application/json", mediaType);
            apiResponse.setContent(content);
            apiResponse.setDescription(returnModel.getDescription());
        }
        else if (returnModel.getChildren() != null && returnModel.getChildren().size() > 0) // 复杂类型
        {
            var schema = new Schema();
            var mediaType = new MediaType();
            mediaType.setSchema(schema);
            var content = new Content();
            content.addMediaType("application/json", mediaType);
            apiResponse.setContent(content);
            apiResponse.setDescription(returnModel.getDescription());

            schema.set$ref("#/components/schemas/" + putSchemaComponent(returnModel.getReturnType(), returnModel.getChildren(), openAPI));
        }
        else
        {
            var schema = new Schema();

            schema.setDescription(returnModel.getDescription());
            schema.setType(_configuration.getTypeInspector().toSwaggerType(returnModel.getReturnType()));
            schema.setFormat(_configuration.getTypeInspector().toSwaggerFormat(returnModel.getReturnType()));

            var mediaType = new MediaType();
            mediaType.setSchema(schema);
            var content = new Content();
            content.addMediaType("application/json", mediaType);
            apiResponse.setContent(content);
            apiResponse.setDescription(returnModel.getDescription());
        }
        return apiResponse;
    }

    private List<Parameter> convertQueryString(ParameterModel paramModel) {
        var parameters = new ArrayList<Parameter>();
        if (paramModel.isArray())
        {
            convertParameterChildren(paramModel.getChildren(), paramModel.getName(), true).stream().forEach(o -> parameters.add(o));
        }
        else if (paramModel.getChildren() == null || paramModel.getChildren().size() == 0)
        {
            var parameter = new Parameter();
            parameter.setName(paramModel.getName());
            parameter.setDescription(paramModel.getDescription());
            parameter.setIn("query");

            parameter.setSchema(generateParameterSchema(paramModel));
        }
        else
        {
            convertParameterChildren(paramModel.getChildren(), null, false).stream().forEach(o -> parameters.add(o));
        }
        return parameters;
    }

    private Schema generateParameterSchema(ParameterModel paramModel) {
        if (ReflectUtils.isEnum(paramModel.getParameterType()))
        {
            return generateEnumSchema((Class)paramModel.getParameterType());
        }
        var schema = new Schema();
        schema.setType(_configuration.getTypeInspector().toSwaggerType(paramModel.getParameterType()));
        schema.setFormat(_configuration.getTypeInspector().toSwaggerFormat(paramModel.getParameterType()));
        return schema;
    }
    private Schema generateParameterSchema(PropertyModel child) {
        if (ReflectUtils.isEnum(child.getPropertyType()))
        {
            return generateEnumSchema((Class)child.getPropertyType());
        }
        var schema = new Schema();
        schema.setType(_configuration.getTypeInspector().toSwaggerType(child.getPropertyType()));
        schema.setFormat(_configuration.getTypeInspector().toSwaggerFormat(child.getPropertyType()));
        // todo array , enum
        return schema;
    }

    private List<Parameter> convertParameterChildren(List<PropertyModel> propertyModels, String prefix, boolean isArray)
    {
        var parameters = new ArrayList<Parameter>();
        String name = "";
        if (prefix != null)
            name = prefix + ".";
        for (var child : propertyModels)
        {
            if (child.isArray())
            {
                convertParameterChildren(child.getChildren(), child.getName(), true).stream().forEach(o -> parameters.add(o));
            }
            else if (child.getChildren() == null || child.getChildren().size() == 0) {
                var parameter = new Parameter();

                parameter.setName(name + child.getName());
                parameter.setDescription(child.getDescription());
                parameter.setIn("query");
                if (isArray)
                {
                    parameter.setSchema(generateArraySchema(child));
                }
                else {
                    parameter.setSchema(generateParameterSchema(child));
                }
                parameters.add(parameter);
            }
            else
            {
                parameters.addAll(convertParameterChildren(child.getChildren(), name + child.getName(), false));
            }
            // todo path
        }
        return parameters;
    }

    private Schema generateArraySchema(PropertyModel child) {
        var arraySchema = new ArraySchema();

        var schema = new Schema();
        schema.setType(_configuration.getTypeInspector().toSwaggerType(child.getPropertyType()));
        schema.setFormat(_configuration.getTypeInspector().toSwaggerFormat(child.getPropertyType()));

        arraySchema.setItems(schema);

        return arraySchema;
    }

    private Parameter convertPathParameter(ParameterModel param) {
        var parameter = new Parameter();

        parameter.setIn("path");
        parameter.setName(param.getName());
        parameter.setDescription(param.getDescription());
        parameter.setSchema(generateParameterSchema(param));

        return parameter;
    }
}
