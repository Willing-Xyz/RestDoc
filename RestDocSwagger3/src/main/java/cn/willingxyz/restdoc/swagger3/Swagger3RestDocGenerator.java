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

    public Swagger3RestDocGenerator(Swagger3GeneratorConfig configuration)
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

    // tag 用于对path进行分组，类似于springmvc的controller
    private void convertTag(RootModel rootModel, OpenAPI openApi) {
        for (var controller : rootModel.getControllers())
        {
            var tag = new Tag();
            tag.setName(_configuration.getTypeNameParser().parse(controller.getControllerClass()));
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
            }
            else if (param.getLocation() == ParameterModel.ParameterLocation.BODY)
            {
                operation.setRequestBody(convertRequestBody(param, openApi));
            }
            else if (param.getLocation() == ParameterModel.ParameterLocation.PATH)
            {
                operation.addParametersItem(convertPathParameter(param));
            }
            else if (param.getLocation() == ParameterModel.ParameterLocation.HEADER)
            {
                operation.addParametersItem(convertHeaderParameter(param));
            }
            else if (param.getLocation() == ParameterModel.ParameterLocation.FILE)
            {
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
        for (var httpMethod : mapping.getHttpMethods())
        {
            switch (httpMethod) {
                case GET:
                    pathItem.get(operation); break;
                case PUT:
                    pathItem.put(operation); break;
                case POST:
                    pathItem.post(operation); break;
                case DELETE:
                    pathItem.delete(operation); break;
                case HEAD:
                    pathItem.head(operation); break;
                case PATCH:
                    pathItem.patch(operation); break;
                case TRACE:
                    pathItem.patch(operation); break;
                case OPTIONS:
                    pathItem.options(operation); break;
            }
        }
    }

    /**
     * 转换RequestBody
     */
    private RequestBody convertRequestBody(ParameterModel parameterModel, OpenAPI openAPI) {
        var requestBody = new RequestBody();

        if (parameterModel.isArray()) {
            // 简单数组类型
            if (parameterModel.getChildren() == null || parameterModel.getChildren().isEmpty())
            {
                var arraySchema = new ArraySchema();
                var itemSchema = new Schema();
                itemSchema.setType(_configuration.getSwaggerTypeInspector().toSwaggerType(_configuration.getTypeInspector().getCollectionComponentType(parameterModel.getParameterType())));
                arraySchema.setItems(itemSchema);

                Content content = createContent(arraySchema);
                requestBody.setContent(content);
                requestBody.setRequired(parameterModel.isRequired());
                requestBody.setDescription(parameterModel.getDescription());
            }
            else { // 复杂数组类型
                var itemSchema = new Schema();
                itemSchema.set$ref("#/components/schemas/" + putSchemaComponent(parameterModel.getParameterType(), parameterModel.getChildren(), openAPI));

                var content = createContent(itemSchema);
                requestBody.setContent(content);
                requestBody.setRequired(parameterModel.isRequired());
                requestBody.setDescription(parameterModel.getDescription());
            }
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
                    if (parameterModel.isRequired()) {
                    schema.setRequired(Arrays.asList(parameterModel.getName()));
                }
            }
            else {
                schema = new Schema();

                schema.setDescription(parameterModel.getDescription());
                schema.setType(_configuration.getSwaggerTypeInspector().toSwaggerType(parameterModel.getParameterType()));
                schema.setFormat(_configuration.getSwaggerTypeInspector().toSwaggerFormat(parameterModel.getParameterType()));

                if (parameterModel.isRequired()) {
                    schema.setRequired(Arrays.asList(parameterModel.getName()));
                }
            }

            var content = createContent(schema);
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
            requestBody.setRequired(parameterModel.isRequired());
            requestBody.setDescription(parameterModel.getDescription());

            schema.set$ref("#/components/schemas/" + putSchemaComponent(parameterModel.getParameterType(), parameterModel.getChildren(), openAPI));
        }
        return requestBody;
    }

    private List<Parameter> convertQueryString(ParameterModel paramModel, OpenAPI openAPI) {
        var parameters = new ArrayList<Parameter>();
        if (paramModel.isArray())
        {
            if (paramModel.getChildren().size() == 0)
            { // 简单类型数据
                addSimpleQueryParameterSchema(paramModel, parameters);
            }
            else
            { // 复杂类型数据
                Parameter parameter = new Parameter();
                parameter.setName(paramModel.getName());
                parameter.setDescription(paramModel.getDescription());
                parameter.setIn("query");

                ArraySchema arraySchema = new ArraySchema();
                arraySchema.setType("array");
                Schema schema = new Schema();
                schema.set$ref(putSchemaComponent(paramModel.getParameterType(), paramModel.getChildren(), openAPI));
                arraySchema.setItems(schema);

                parameter.setSchema(arraySchema);
                parameters.add(parameter);
            }
        }
        else if (paramModel.getChildren() == null || paramModel.getChildren().size() == 0) // 简单类型
        {
            addSimpleQueryParameterSchema(paramModel, parameters);
        }
        else // 复杂对象
        {
            convertParameterChildren(paramModel.getChildren(), null, paramModel.getDescription(), paramModel.isRequired(), false)
                    .stream().forEach(o -> parameters.add(o));
        }
        return parameters;
    }

    private Parameter convertPathParameter(ParameterModel param) {
        var parameter = new Parameter();

        parameter.setIn("path");
        parameter.setName(param.getName());
        parameter.setDescription(param.getDescription());
        parameter.setSchema(generateSimpleParameterSchema(param));

        return parameter;
    }

    private Parameter convertHeaderParameter(ParameterModel paramModel) {
        var parameter = new Parameter();

        parameter.setIn("header");
        parameter.setName(paramModel.getName());
        parameter.setDescription(paramModel.getDescription());

        parameter.setSchema(generateSimpleParameterSchema(paramModel));
        return parameter;
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

        if (returnModel.isArray()) {
            if (returnModel.getChildren() == null || returnModel.getChildren().isEmpty())
            {
                var schema = generateSimpleArraySchema(returnModel);
                apiResponse.setContent(createContent(schema));
            }
            else {
                var itemSchema = new Schema();
                itemSchema.setDescription(returnModel.getDescription());
                itemSchema.set$ref("#/components/schemas/" + putSchemaComponent(returnModel.getReturnType(), returnModel.getChildren(), openAPI));

                Content content = createContent(itemSchema);
                apiResponse.setContent(content);
            }
        }
        else if (returnModel.getChildren() != null && returnModel.getChildren().size() > 0) // 复杂类型
        {
            var schema = new Schema();
            var content = createContent(schema);
            apiResponse.setContent(content);

            schema.set$ref("#/components/schemas/" + putSchemaComponent(returnModel.getReturnType(), returnModel.getChildren(), openAPI));
        }
        else // 简单类型
        {
            var schema = new Schema();
            if (ReflectUtils.isEnum(returnModel.getReturnType()))
            {
                schema = generateEnumSchema((Class)returnModel.getReturnType());
            }
            else {
                schema.setDescription(returnModel.getDescription());
                schema.setType(_configuration.getSwaggerTypeInspector().toSwaggerType(returnModel.getReturnType()));
                schema.setFormat(_configuration.getSwaggerTypeInspector().toSwaggerFormat(returnModel.getReturnType()));
            }

            var content = createContent(schema);
            apiResponse.setContent(content);
            apiResponse.setDescription(returnModel.getDescription());
        }
        return apiResponse;
    }

    // 生成Schema（复杂对象），并放入openapi中。
    private String putSchemaComponent(Type type, List<PropertyModel> children, OpenAPI openAPI) {
        var className = getComponentName(type);

        if (openAPI.getComponents() == null)
            openAPI.components(new Components());
        if (openAPI.getComponents().getSchemas() == null)
            openAPI.getComponents().schemas(new HashMap<>());
        if (openAPI.getComponents().getSchemas().containsKey(className))
        {
            return className;
        }
        var schema = generateSchemaComponent(type, children, openAPI);

        openAPI.getComponents().addSchemas(className, schema);

        return className;
    }

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
        }
        else
        {
            return convertCollectionName(collectionCount, name, ',');
        }
    }

    private String convertCollectionName(int collectionCount, String name, char ch) {
        String prefix = "";
        int index = name.lastIndexOf(ch);
        if (index != -1)
        {
            prefix = name.substring(0, index + 1);
            name = name.substring(index + 1);
        }
        while (collectionCount-- > 0)
        {
            name = "[" + name;
        }
        return prefix + name;
    }

    // 生成复杂对象的schema
    private Schema generateSchemaComponent(Type type, List<PropertyModel> children, OpenAPI openAPI) {

        if (_configuration.getTypeInspector().isCollection(type))
        {
            var arraySchema = new ArraySchema();
            arraySchema.setItems(generateSchemaComponent(_configuration.getTypeInspector().getCollectionComponentType(type), children, openAPI));
            return arraySchema;
        }
        if (ReflectUtils.isEnum(type))
        {
            return generateEnumSchema((Class) type);
        }
        var schema = new Schema();
        var className = _configuration.getTypeNameParser().parse(type);
        schema.setName(className);
        schema.setType(_configuration.getSwaggerTypeInspector().toSwaggerType(type));


        var classDoc = RuntimeJavadoc.getJavadoc(type.getTypeName());
        schema.setDescription(FormatUtils.format(classDoc.getComment()));

        schema.setProperties(generateSchemaProperty(children, openAPI));

        return schema;
    }
    private Map<String, Schema> generateSchemaProperty(List<PropertyModel> propertyModels, OpenAPI openAPI) {
        var schemas = new HashMap<String, Schema>();

        for (var propertyModel : propertyModels) {
            var schema = new Schema();
            if (_configuration.getTypeInspector().isCollection(propertyModel.getPropertyType()))
            {
                var arraySchema = new ArraySchema();
                arraySchema.setItems(generateSchemaComponent(_configuration.getTypeInspector().getCollectionComponentType(propertyModel.getPropertyType()), propertyModel.getChildren(), openAPI));
                schema = arraySchema;
            }
            else if (ReflectUtils.isEnum(propertyModel.getPropertyType()))
            {
                schema = generateEnumSchema((Class)propertyModel.getPropertyType());
            }
            else if (propertyModel.getChildren() == null || propertyModel.getChildren().size() == 0)
            {
//                schema.setTitle(parameterModel.getPropertyType().getCanonicalName());
                schema.setDescription(propertyModel.getDescription());

                if (ReflectUtils.isEnum(propertyModel.getPropertyType())) {
                    var clazz = (Class)propertyModel.getPropertyType();
                    schema = generateEnumSchema(clazz);
                }
                else {
                    schema.setType(_configuration.getSwaggerTypeInspector().toSwaggerType(propertyModel.getPropertyType()));
                    schema.setFormat(_configuration.getSwaggerTypeInspector().toSwaggerFormat(propertyModel.getPropertyType()));
                }

                if (propertyModel.getRequired() != null && propertyModel.getRequired()) {
                    schema.setRequired(Arrays.asList(propertyModel.getName()));
                }
            }
            else
            {
                schema.set$ref("#/components/schemas/" + putSchemaComponent(propertyModel.getPropertyType(), propertyModel.getChildren(), openAPI));
            }
            schemas.put(propertyModel.getName(), schema);
        }
        return schemas;
    }
    private ArraySchema generateEnumSchema(Class clazz) {
        var arraySchema = new ArraySchema();
        arraySchema.setType("string");
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

    private Schema generateSimpleArraySchema(ReturnModel returnModel) {
        var arraySchema = new ArraySchema();
        arraySchema.setDescription(returnModel.getDescription());
        var itemSchema = new Schema();
        itemSchema.setDescription(returnModel.getDescription());
        if (ReflectUtils.isEnum(_configuration.getTypeInspector().getCollectionComponentType(returnModel.getReturnType())))
        {
            itemSchema = generateEnumSchema((Class) _configuration.getTypeInspector().getCollectionComponentType(returnModel.getReturnType()));
        }
        else {
            itemSchema.setType(_configuration.getSwaggerTypeInspector().toSwaggerType(_configuration.getTypeInspector().getCollectionComponentType(returnModel.getReturnType())));
            itemSchema.setFormat(_configuration.getSwaggerTypeInspector().toSwaggerFormat(returnModel.getReturnType()));
        }

        arraySchema.setItems(itemSchema);

        return arraySchema;
    }

    private Content createContent(Schema schema) {
        var mediaType = new MediaType();
        mediaType.setSchema(schema);
        var content = new Content();
        content.addMediaType("application/json", mediaType);
        return content;
    }

    private void addSimpleQueryParameterSchema(ParameterModel paramModel, ArrayList<Parameter> parameters) {
        var parameter = new Parameter();
        parameter.setName(paramModel.getName());
        parameter.setDescription(paramModel.getDescription());
        parameter.setIn("query");

        parameter.setSchema(generateSimpleParameterSchema(paramModel));
        parameters.add(parameter);
    }

    private Schema generateSimpleParameterSchema(ParameterModel paramModel) {
        if (ReflectUtils.isEnum(paramModel.getParameterType()))
        {
            return generateEnumSchema((Class)paramModel.getParameterType());
        }
        if (_configuration.getTypeInspector().isCollection(paramModel.getParameterType()))
        {
            return generateArraySchema(paramModel);
        }
        var schema = new Schema();
        schema.setType(_configuration.getSwaggerTypeInspector().toSwaggerType(paramModel.getParameterType()));
        schema.setFormat(_configuration.getSwaggerTypeInspector().toSwaggerFormat(paramModel.getParameterType()));

        return schema;
    }

    private Schema generateArraySchema(ParameterModel paramModel) {
        var arraySchema = new ArraySchema();

        var schema = new Schema();
        schema.setType(_configuration.getSwaggerTypeInspector().toSwaggerType(_configuration.getTypeInspector().getCollectionComponentType(paramModel.getParameterType())));
        schema.setFormat(_configuration.getSwaggerTypeInspector().toSwaggerFormat(paramModel.getParameterType()));

        arraySchema.setItems(schema);

        return arraySchema;
    }

    private Schema generateSimpleParameterSchema(PropertyModel child) {
        if (ReflectUtils.isEnum(child.getPropertyType()))
        {
            return generateEnumSchema((Class)child.getPropertyType());
        }
        if (_configuration.getTypeInspector().isCollection(child.getPropertyType()))
        {
            return generateArraySchema(child);
        }
        var schema = new Schema();
        schema.setType(_configuration.getSwaggerTypeInspector().toSwaggerType(child.getPropertyType()));
        schema.setFormat(_configuration.getSwaggerTypeInspector().toSwaggerFormat(child.getPropertyType()));
        return schema;
    }

    private List<Parameter> convertParameterChildren(List<PropertyModel> propertyModels, String paraName, String desc, Boolean required, boolean isArray)
    {
        var parameters = new ArrayList<Parameter>();
        String name = "";
        if (paraName != null)
            name = paraName + ".";
        for (var child : propertyModels)
        {
            if (child.isArray())
            {
//                convertParameterChildren(child.getChildren(), child.getName(), true).stream().forEach(o -> parameters.add(o));
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
                    parameter.setSchema(generateSimpleParameterSchema(child));
                }
                parameters.add(parameter);
            }
            else
            {
//                parameters.addAll(convertParameterChildren(child.getChildren(), name + child.getName(), false));
            }
            // todo path
        }
        return parameters;
    }

    private Schema generateArraySchema(PropertyModel child) {
        var arraySchema = new ArraySchema();

        var schema = new Schema();
        schema.setType(_configuration.getSwaggerTypeInspector().toSwaggerType(child.getPropertyType()));
        schema.setFormat(_configuration.getSwaggerTypeInspector().toSwaggerFormat(child.getPropertyType()));

        arraySchema.setItems(schema);

        return arraySchema;
    }
}
