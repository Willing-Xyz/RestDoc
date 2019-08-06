package cn.willingxyz.restdoc.swagger3;

import cn.willingxyz.restdoc.core.parse.utils.FormatUtils;
import cn.willingxyz.restdoc.core.parse.utils.ReflectUtils;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import cn.willingxyz.restdoc.core.models.*;
import cn.willingxyz.restdoc.core.parse.IRestDocGenerator;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
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

public class Swagger3RestDocGenerator implements IRestDocGenerator {

    public Swagger3GeneratorConfig _configuration;

    public Swagger3RestDocGenerator(Swagger3GeneratorConfig configuration) {
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
        for (var server : _configuration.getServers()) {
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

    // tag 用于对path进行分组，类似于springmvc的controller
    private void convertTag(RootModel rootModel, OpenAPI openApi) {
        for (var controller : rootModel.getControllers()) {
            var tag = new Tag();
            tag.setName(_configuration.getTypeNameParser().parse(controller.getControllerClass()));
            tag.setDescription(controller.getDescription());
            openApi.addTagsItem(tag);
        }
    }

    private void convertPath(RootModel rootModel, OpenAPI openApi) {
        for (var controller : rootModel.getControllers()) {
            for (var method : controller.getControllerMethods()) {
                for (var mapping : method.getMappings()) {
                    convertSinglePath(openApi, controller, method, mapping);
                }
            }
        }
    }

    private void convertSinglePath(OpenAPI openApi, ControllerModel controller, PathModel method, MappingModel mapping) {
        var pathItem = new PathItem();
        pathItem.setDescription(method.getDescription());
        // summary 属于简短的描述
        pathItem.setSummary(TextUtils.getFirstLine(method.getDescription()));

        var operation = new Operation();
        operation.addTagsItem(_configuration.getTypeNameParser().parse(controller.getControllerClass()));
        operation.setSummary(TextUtils.getFirstLine(method.getDescription()));
        operation.setDescription(method.getDescription());
        operation.setDeprecated(method.getDeprecated());

        // 参数解析
        for (var param : method.getParameters()) {
            if (param.getLocation() == ParameterModel.ParameterLocation.QUERY) {
                convertQueryString(param, openApi).forEach(o -> operation.addParametersItem(o));
            } else if (param.getLocation() == ParameterModel.ParameterLocation.BODY) {
                operation.setRequestBody(convertRequestBody(param, openApi));
            } else if (param.getLocation() == ParameterModel.ParameterLocation.PATH) {
                operation.addParametersItem(generateSingleParameterSchema("path", param, openApi));
            } else if (param.getLocation() == ParameterModel.ParameterLocation.HEADER) {
                operation.addParametersItem(generateSingleParameterSchema("header", param, openApi));
            } else if (param.getLocation() == ParameterModel.ParameterLocation.FILE) {
                operation.setRequestBody(convertFileParameter(param));
            }
        }
        // 响应解析
        operation.setResponses(convertResponses(method, openApi));

        setHttpMethod(mapping, pathItem, operation);

        for (var path : mapping.getPaths()) {
            openApi.path(path, pathItem);
        }
    }

    private void setHttpMethod(MappingModel mapping, PathItem pathItem, Operation operation) {
        for (var httpMethod : mapping.getHttpMethods()) {
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
    }

    /**
     * 转换RequestBody
     */
    private RequestBody convertRequestBody(ParameterModel parameterModel, OpenAPI openAPI) {
        var requestBody = new RequestBody();
        requestBody.setRequired(parameterModel.isRequired());
        requestBody.setDescription(parameterModel.getDescription());

        Schema contentSchema = generateSchema(parameterModel.getDescription(), parameterModel.getParameterType(), parameterModel.getChildren(), openAPI);

        requestBody.setContent(createContent(contentSchema));
        return requestBody;
    }

    private List<Parameter> convertQueryString(ParameterModel paramModel, OpenAPI openAPI) {
        var parameters = new ArrayList<Parameter>();
        if (paramModel.getChildren() != null && paramModel.getChildren().size() > 0) // 复杂对象
        {
            convertParameterChildren(paramModel.getChildren(), null, openAPI)
                    .stream().forEach(o -> parameters.add(o));
        } else {
            parameters.add(generateSingleParameterSchema("query", paramModel, openAPI));
        }
        return parameters;
    }

    private RequestBody convertFileParameter(ParameterModel param) {
        var requestBody = new RequestBody();

        var propSchema = new Schema();
        propSchema.setType("string");
        propSchema.format("binary");

        Schema schema = new Schema();
        schema.setType("object");
        schema.addProperties(param.getName(), propSchema);

        Content content = new Content();
        var mediaType = new MediaType();
        mediaType.setSchema(schema);
        content.addMediaType("multipart/form-data", mediaType);

        requestBody.setContent(content);
        requestBody.setDescription(param.getDescription());
        requestBody.setRequired(param.isRequired());

        return requestBody;
    }

    private ApiResponse convertResponse(ResponseModel responseModel, OpenAPI openAPI) {
        var apiResponse = new ApiResponse();
        var returnModel = responseModel.getReturnModel();

        apiResponse.setDescription(returnModel.getDescription());
        if (returnModel.getReturnType() == void.class || returnModel.getReturnType() == Void.class)
            return apiResponse;

        Schema schema = generateSchema(returnModel.getDescription(), returnModel.getReturnType(), returnModel.getChildren(), openAPI);

        apiResponse.setContent(createContent(schema));
        return apiResponse;
    }

    private ApiResponses convertResponses(PathModel method, OpenAPI openAPI) {
        var response = new ApiResponses();
        for (var res : method.getResponse()) {
            ApiResponse apiResponse = convertResponse(res, openAPI);
            response.addApiResponse(res.getStatusCode() + "", apiResponse);
        }
        return response;
    }

    private Content createContent(Schema schema) {
        var mediaType = new MediaType();
        mediaType.setSchema(schema);
        var content = new Content();
        content.addMediaType("application/json", mediaType);
        return content;
    }

    private Parameter generateSingleParameterSchema(String in, ParameterModel paramModel, OpenAPI openAPI) {
        var parameter = new Parameter();
        parameter.setName(paramModel.getName());
        parameter.setDescription(paramModel.getDescription());
        parameter.setIn(in);

        parameter.setSchema(generateSchema(paramModel.getDescription(), paramModel.getParameterType(), paramModel.getChildren(), openAPI));
        return parameter;
    }

    private List<Parameter> convertParameterChildren(List<PropertyModel> propertyModels, String paraName, OpenAPI openAPI) {
        var parameters = new ArrayList<Parameter>();
        String name = "";
        if (paraName != null)
            name = paraName + ".";
        for (var child : propertyModels) {
            if (child.isArray()) {
                convertParameterChildren(child.getChildren(), child.getName(), openAPI).stream().forEach(o -> parameters.add(o));
            } else if (child.getChildren() == null || child.getChildren().size() == 0) {
                var parameter = new Parameter();

                parameter.setName(name + child.getName());
                parameter.setDescription(child.getDescription());
                parameter.setIn("query");

                parameter.setSchema(generateSchema(child.getDescription(), child.getPropertyType(), child.getChildren(), openAPI));
                parameters.add(parameter);
            } else {
                parameters.addAll(convertParameterChildren(child.getChildren(), name + child.getName(), openAPI));
            }
            // todo path
        }
        return parameters;
    }

    // ----------------------core---------------

//    // 生成Schema，并放入openapi中。
//    private String putSchemaComponent(String description, Type type, List<PropertyModel> children, OpenAPI openAPI) {
//        var className = getComponentName(type);
//
//        if (openAPI.getComponents() == null)
//            openAPI.components(new Components());
//        if (openAPI.getComponents().getSchemas() == null)
//            openAPI.getComponents().schemas(new HashMap<>());
//        if (openAPI.getComponents().getSchemas().containsKey(className)) {
//            return className;
//        }
//        var schema = generateSchema(description, type, children, openAPI);
//
//        openAPI.getComponents().addSchemas(className, schema);
//
//        return className;
//    }

    // 生成schema
    private Schema generateSchema(String description, Type type, List<PropertyModel> children, OpenAPI openAPI) {
        // 数组/集合
        if (_configuration.getTypeInspector().isCollection(type)) {
            return generateArraySchema(description, type, children, openAPI);
        }
        // 枚举
        if (ReflectUtils.isEnum(type)) {
            return generateEnumSchema((Class) type);
        }
        // 简单类型
        if (children == null || children.isEmpty()) {
            return generateSimpleTypeSchema(description, type);
        }
        // 复杂类型
        return generateComplexTypeSchema(type, children, openAPI);
    }

    private Schema generateArraySchema(String description, Type type, List<PropertyModel> children, OpenAPI openAPI) {
        var arraySchema = new ArraySchema();
        arraySchema.setDescription(description);
        arraySchema.setItems(generateSchema(description, _configuration.getTypeInspector().getCollectionComponentType(type), children, openAPI));
        return arraySchema;
    }

    private Schema generateComplexTypeSchema(Type type, List<PropertyModel> children, OpenAPI openAPI) {
        var schema = new Schema();
        var classDoc = RuntimeJavadoc.getJavadoc(type.getTypeName());
        schema.setDescription(FormatUtils.format(classDoc.getComment()));
        schema.setProperties(generateComplexTypeSchemaProperty(type, children, openAPI));

        return schema;
    }

    private Map<String, Schema> generateComplexTypeSchemaProperty(Type type, List<PropertyModel> propertyModels, OpenAPI openAPI) {
        var schemas = new HashMap<String, Schema>();

        for (var propertyModel : propertyModels) {
            var schema = generateSchema(propertyModel.getDescription(), propertyModel.getPropertyType(), propertyModel.getChildren(), openAPI);
            schemas.put(propertyModel.getName(), schema);
        }
        return schemas;
    }

    private Schema generateSimpleTypeSchema(String description, Type type) {
        var schema = new Schema();
        schema.setDescription(description);
        schema.setType(_configuration.getSwaggerTypeInspector().toSwaggerType(type));
        schema.setFormat(_configuration.getSwaggerTypeInspector().toSwaggerFormat(type));
        return schema;
    }

    private Schema generateEnumSchema(Class clazz) {
        var enumDoc = RuntimeJavadoc.getJavadoc(clazz);
        var enums = Arrays.stream(clazz.getEnumConstants()).map(o -> o.toString()).collect(Collectors.toList());

        var itemSchema = new Schema();

        itemSchema.setDescription(FormatUtils.format(enumDoc.getComment()));
        itemSchema.setType("string"); // todo 如何决定是string还是int
        itemSchema.setEnum(enums);
        if (enums.size() > 0) {
            itemSchema.setDefault(enums.get(0));
        }
        return itemSchema;
    }

    // --------------------utils----------------
    // todo 提取到Common

    // List<String> -> [String
    // List<List<String> -> [[String
    // com.willing.List<String> -> com.willing.[String
    private String getComponentName(Type type) {
        // 统计嵌套集合的深度
        int collectionCount = 0;
        while (_configuration.getTypeInspector().isCollection(type)) {
            collectionCount++;
            type = _configuration.getTypeInspector().getCollectionComponentType(type);
        }
        var name = _configuration.getTypeNameParser().parse(type);
        int $index = name.lastIndexOf('$');
        int dotIndex = name.lastIndexOf('.');
        if ($index > dotIndex) {
            return convertCollectionName(collectionCount, name, '$');
        } else {
            return convertCollectionName(collectionCount, name, ',');
        }
    }

    private String convertCollectionName(int collectionCount, String name, char ch) {
        String prefix = "";
        int index = name.lastIndexOf(ch);
        if (index != -1) {
            prefix = name.substring(0, index + 1);
            name = name.substring(index + 1);
        }
        while (collectionCount-- > 0) {
            name = "[" + name;
        }
        return prefix + name;
    }
}
