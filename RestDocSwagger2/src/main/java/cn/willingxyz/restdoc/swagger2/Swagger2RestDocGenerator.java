package cn.willingxyz.restdoc.swagger2;

import cn.willingxyz.restdoc.core.models.*;
import cn.willingxyz.restdoc.core.parse.IRestDocGenerator;
import cn.willingxyz.restdoc.core.parse.utils.FormatUtils;
import cn.willingxyz.restdoc.core.parse.utils.ReflectUtils;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;
import cn.willingxyz.restdoc.swagger.common.utils.ClassNameUtils;
import cn.willingxyz.restdoc.swagger.common.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import io.swagger.models.*;
import io.swagger.models.parameters.*;
import io.swagger.models.properties.*;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static cn.willingxyz.restdoc.swagger.common.utils.StringUtils.combineStr;

public class Swagger2RestDocGenerator implements IRestDocGenerator {
    private static Logger logger = LoggerFactory.getLogger(Swagger2RestDocGenerator.class);

//    public static void main(String[] args) {
//
//        SwaggerGeneratorConfig generatorConfig = SwaggerGeneratorConfig.builder()
//                .servers(Arrays.asList(SwaggerGeneratorConfig.ServerInfo.builder().description("server").url("/")
//                        .build()))
//                .swaggerTypeInspector(new PrimitiveSwaggerTypeInspector())
//                .typeInspector(new JavaTypeInspector())
//                .typeNameParser(new TypeNameParser())
//                .build();
//        var generator = new Swagger2RestDocGenerator(generatorConfig);
//
//        var parseConfig = new RestDocParseConfig();
//        parseConfig.setRestDocGenerator(generator);
//
//        var parser = new RestDocParser(parseConfig);
//
//        System.out.println(parser.parse());
//    }

    private final Swagger2GeneratorConfig _config;

    public Swagger2RestDocGenerator(Swagger2GeneratorConfig config) {
        _config = config;
    }

    @Override
    public String generate(RootModel rootModel) {
        var swagger = generateSwagger(rootModel);

        if (_config.getSwaggerFilters() != null) {
            for (ISwaggerFilter openAPIFilter : _config.getSwaggerFilters()) {
                swagger = openAPIFilter.handle(swagger);
            }
        }

        var objectMapper = JsonUtils.objectMapper();
        try {
            return objectMapper.writeValueAsString(swagger);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化错误");
        }
    }

    private Swagger generateSwagger(RootModel rootModel) {
        var swagger = new Swagger();

        convertServers(rootModel, swagger);
        convertInfo(rootModel, swagger);
        convertTag(rootModel, swagger);
        convertPath(rootModel, swagger);

        return swagger;
    }

    private void convertTag(RootModel rootModel, Swagger swagger) {
        for (var controller : rootModel.getControllers()) {
            var tag = new Tag();

            tag.setName(getTagName(controller));
            tag.setDescription(controller.getDescription());

            swagger.addTag(tag);
        }
    }

    private void convertInfo(RootModel rootModel, Swagger swagger) {
        var info = new Info();
        info.setDescription(_config.getDescription());
        info.setVersion(_config.getVersion());
        info.setTitle(_config.getTitle());
        swagger.setInfo(info);
    }

    private void convertServers(RootModel rootModel, Swagger swagger) {
        if (_config.getServers() == null || _config.getServers().isEmpty()) return;
        // 把传递的url拆分为schema、host:port 和 basePath
        String url = _config.getServers().get(0).getUrl();

        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("ws://") && !url.startsWith("wss://"))
            url = "http://" + url;

        try {
            URL urlObj = new URL(url);
            if (urlObj.getPort() == -1)
                swagger.setHost(urlObj.getHost());
            else
                swagger.setHost(urlObj.getHost() + ":" + urlObj.getPort());

            swagger.setBasePath(urlObj.getPath());
        } catch (MalformedURLException e) {
            logger.warn("invalid server url: " + url);
        }

        _config.getServers().forEach(o -> {
            try {
                URL urlObj = new URL(o.getUrl());
                Scheme scheme = Scheme.forValue(urlObj.getProtocol());
                if (scheme != null) {
                    if (swagger.getSchemes() == null)
                        swagger.setSchemes(new ArrayList<>());
                    swagger.getSchemes().add(scheme);
                }
            } catch (MalformedURLException e) {
                logger.warn("invalid server url: " + o.getUrl());
            }
        });
    }

    private void convertPath(RootModel rootModel, Swagger swagger) {
        for (var controller : rootModel.getControllers()) {
            for (var method : controller.getControllerMethods()) {
                for (var mapping : method.getMappings()) {
                    convertSinglePath(swagger, controller, method, mapping);
                }
            }
        }
    }

    private void convertSinglePath(Swagger swagger, ControllerModel controller, PathModel method, MappingModel mapping) {
        var operation = new Operation();
        operation.addTag(getTagName(controller));
        operation.setSummary(TextUtils.getFirstLine(method.getDescription()));
        operation.setDescription(method.getDescription());
        operation.setDeprecated(method.getDeprecated());

        // 参数解析
        for (var param : method.getParameters()) {
            convertParameter(swagger, operation, param);
        }
        // 响应解析
        operation.setResponses(convertResponses(method, swagger));

        for (var pathItem : mapping.getPaths()) {
            Path path = null;
            if (swagger.getPaths() != null) {
                path = swagger.getPaths().entrySet().stream()
                        .filter(o -> o.getKey().equals(pathItem))
                        .map(o -> o.getValue())
                        .findFirst()
                        .orElse(null);
            }
            if (path == null)
                path = new Path();
            setHttpMethod(mapping, path, operation);

            swagger.path(pathItem, path);
        }
    }

    private void convertParameter(Swagger swagger, Operation operation, ParameterModel param) {
        if (param.getLocation() == ParameterModel.ParameterLocation.QUERY) {
            convertQueryString(param, swagger).forEach(o -> operation.addParameter(o));
        } else if (param.getLocation() == ParameterModel.ParameterLocation.BODY) {
            operation.addParameter(convertRequestBody(param, swagger));
        } else if (param.getLocation() == ParameterModel.ParameterLocation.PATH) {
            operation.addParameter(generateSingleParameterSchema("path", param, swagger));
        } else if (param.getLocation() == ParameterModel.ParameterLocation.HEADER) {
            operation.addParameter(generateSingleParameterSchema("header", param, swagger));
        } else if (param.getLocation() == ParameterModel.ParameterLocation.FILE) {
            operation.consumes("multipart/form-data");
            operation.addParameter(convertFileParameter(param));
        }
    }

    private Map<String, Response> convertResponses(PathModel method, Swagger swagger) {
        var responses = new LinkedHashMap<String, Response>();
        for (var res : method.getResponse()) {
            Response apiResponse = convertResponse(res, swagger);
            responses.put(res.getStatusCode() + "", apiResponse);
        }
        return responses;
    }

    private Response convertResponse(ResponseModel responseModel, Swagger swagger) {
        var apiResponse = new Response();
        var returnModel = responseModel.getReturnModel();

        apiResponse.setDescription(returnModel.getDescription());
        if (returnModel.getReturnType() == void.class || returnModel.getReturnType() == Void.class)
            return apiResponse;

        apiResponse.setResponseSchema(getOrGenerateModel(returnModel.getEnums(), false, returnModel.getExample(), returnModel.getDescription(), returnModel.getReturnType(), returnModel.getChildren(), swagger));
        Property property = generateProperty(returnModel.getEnums(), false, returnModel.getExample(), returnModel.getDescription(), returnModel.getReturnType(), returnModel.getChildren(), swagger);
        apiResponse.setSchema(property);

        apiResponse.setDescription(combineStr(apiResponse.getDescription(), property.getDescription()));

        return apiResponse;
    }


    private Parameter convertFileParameter(ParameterModel param) {
        var formDataParameter = new FormParameter();

        formDataParameter.setType("file");
        formDataParameter.setName(param.getName());
        formDataParameter.setDescription(param.getDescription());
        formDataParameter.setExample(param.getExample());

        formDataParameter.setRequired(param.isRequired());

        return formDataParameter;
    }

    private Parameter convertRequestBody(ParameterModel parameterModel, Swagger swagger) {
        BodyParameter bodyParameter = new BodyParameter();
        bodyParameter.setName(parameterModel.getName());
        bodyParameter.setDescription(parameterModel.getDescription());
        bodyParameter.setRequired(parameterModel.isRequired());

        Model model = getOrGenerateModel(parameterModel.getEnums(), parameterModel.isRequired(), parameterModel.getExample(), parameterModel.getDescription(), parameterModel.getParameterType(), parameterModel.getChildren(), swagger);
        bodyParameter.setSchema(model);

        return bodyParameter;
    }

    private Model getOrGenerateModel(List<String> enums, boolean required, String example, String description, Type parameterType, List<PropertyModel> children, Swagger swagger) {
        var componentName = ClassNameUtils.getComponentName(_config.getTypeInspector(), _config.getTypeNameParser(), parameterType);

        if (swagger.getDefinitions() == null)
            swagger.setDefinitions(new LinkedHashMap<>());
        if (!swagger.getDefinitions().containsKey(componentName)) {
            Property property = generateProperty(enums, required, example, description, parameterType, children, swagger);
            Model model = null;
            if (property instanceof ObjectProperty) {
                model = new ModelImpl();
                model.setProperties(((ObjectProperty) property).getProperties());
            } else if (property instanceof ArrayProperty) {
                ArrayModel arrayModel = new ArrayModel();
                arrayModel.setItems(property);
                model = arrayModel;
            } else {
                ModelImpl modelImpl = new ModelImpl();
                modelImpl.setName(property.getName());
                modelImpl.setDescription(property.getDescription());
                modelImpl.setTitle(property.getTitle());
                modelImpl.setType(property.getType());
                modelImpl.setFormat(property.getFormat());

                if (property instanceof StringProperty) {
                    modelImpl.setEnum(((StringProperty) property).getEnum());
                    modelImpl.setDefaultValue(((StringProperty) property).getDefault());
                }
                model = modelImpl;
            }
            model.setDescription(property.getDescription());
            swagger.getDefinitions().put(componentName, model);
        }
        return swagger.getDefinitions().get(componentName);
    }

    private void setHttpMethod(MappingModel mapping, Path pathItem, Operation operation) {
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
                case OPTIONS:
                    pathItem.options(operation);
                    break;
            }
        }
    }

    private List<Parameter> convertQueryString(ParameterModel paramModel, Swagger swagger) {
        var parameters = new ArrayList<Parameter>();
        if (paramModel.getChildren() != null && paramModel.getChildren().size() > 0) // 复杂对象
        {
            convertParameterChildren(paramModel.getChildren(), null, swagger).forEach(o -> parameters.add(o));
        } else {
            parameters.add(generateSingleParameterSchema("query", paramModel, swagger));
        }
        return parameters;
    }

    private List<Parameter> convertParameterChildren(List<PropertyModel> propertyModels, String paraName, Swagger swagger) {
        var parameters = new ArrayList<Parameter>();
        String name = "";
        if (paraName != null)
            name = paraName + ".";
        for (var child : propertyModels) {
            if (child.isArray()) {
                convertParameterChildren(child.getChildren(), child.getName(), swagger).forEach(o -> parameters.add(o));
            } else if (child.getChildren() == null || child.getChildren().size() == 0) {
                var parameter = new GenericParameter();

                parameter.setName(name + child.getName());
                parameter.setDescription(child.getDescription());
                parameter.setExample(child.getExample());
                parameter.setIn("query");
                parameter.setRequired(child.isRequired());

                Property property = generateProperty(child.getEnums(), child.isRequired(), child.getExample(), child.getDescription(), child.getPropertyType(), child.getChildren(), swagger);
                parameter.setProperty(property);
                parameter.setDescription(combineStr(parameter.getDescription(), property.getDescription()));
                parameters.add(parameter);
            } else {
                parameters.addAll(convertParameterChildren(child.getChildren(), name + child.getName(), swagger));
            }
        }
        return parameters;
    }

    private Parameter generateSingleParameterSchema(String in, ParameterModel paramModel, Swagger swagger) {
        GenericParameter parameter = new GenericParameter();
        parameter.setIn(in);
        parameter.setName(paramModel.getName());
        parameter.setDescription(paramModel.getDescription());
        parameter.setExample(paramModel.getExample());
        parameter.setRequired(paramModel.isRequired());

        Property property = generateProperty(paramModel.getEnums(), paramModel.isRequired(), paramModel.getExample(), paramModel.getDescription(), paramModel.getParameterType(), paramModel.getChildren(), swagger);
        parameter.setProperty(property);
        parameter.setDescription(combineStr(parameter.getDescription(), property.getDescription()));

        return parameter;
    }

    private Property generateProperty(List<String> enums, boolean required, String example, String description, Type type, List<PropertyModel> children, Swagger swagger) {
        // 数组/集合
        if (_config.getTypeInspector().isCollection(type)) {
            return generateArrayProperty(enums, required, example, description, type, children, swagger);
        }
        // 枚举
        if (enums != null) {
            return generateEnumProperty((Class) type, enums, description);
        }
        // 简单类型
        if (children == null || children.isEmpty()) {
            return generateSimpleTypeSchema(required, example, description, type);
        }
        // 复杂类型
        return generateComplexSchema(type, children, swagger);
    }

    private Property generateComplexSchema(Type type, List<PropertyModel> children, Swagger swagger) {
        var schema = new ObjectProperty();
        var classDoc = RuntimeJavadoc.getJavadoc(type.getTypeName());
        schema.setDescription(FormatUtils.format(classDoc.getComment()));
        schema.setProperties(generateComplexTypeSchemaProperty(type, children, swagger));

        return schema;
    }

    private Map<String, Property> generateComplexTypeSchemaProperty(Type type, List<PropertyModel> propertyModels, Swagger swagger) {
        var schemas = new LinkedHashMap<String, Property>();

        for (var propertyModel : propertyModels) {
            var schema = generateProperty(propertyModel.getEnums(), propertyModel.isRequired(), propertyModel.getExample(), propertyModel.getDescription(), propertyModel.getPropertyType(), propertyModel.getChildren(), swagger);
            schemas.put(propertyModel.getName(), schema);
        }
        return schemas;
    }

    private Property generateSimpleTypeSchema(boolean required, String example, String description, Type type) {
        var property = new ObjectProperty();
        property.setDescription(description);
        property.setType(_config.getSwaggerTypeInspector().toSwaggerType(type));
        property.setFormat(_config.getSwaggerTypeInspector().toSwaggerFormat(type));
        property.setExample(example);
        property.setRequired(required);
        return property;
    }

    private Property generateArrayProperty(List<String> enums, boolean required, String example, String description, Type type, List<PropertyModel> children, Swagger swagger) {
        ArrayProperty arrayProperty = new ArrayProperty();
        arrayProperty.setDescription(description);
        Property property = generateProperty(enums, required, example, description, _config.getTypeInspector().getCollectionComponentType(type), children, swagger);
        arrayProperty.setItems(property);
        arrayProperty.setDescription(combineStr(property.getDescription(), arrayProperty.getDescription()));
        return arrayProperty;
    }

    private Property generateEnumProperty(Class clazz, List<String> enums, String description) {
        var property = new StringProperty();
        property.setDescription(description);
        property.setEnum(enums);
        return property;
    }


    private String getTagName(ControllerModel controller) {
        return _config.getTypeNameParser().parse(controller.getControllerClass());
    }
}
