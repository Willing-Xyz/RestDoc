package cn.willingxyz.restdoc.swagger2;

import cn.willingxyz.restdoc.core.models.*;
import cn.willingxyz.restdoc.core.parse.IRestDocGenerator;
import cn.willingxyz.restdoc.core.parse.RestDocParseConfig;
import cn.willingxyz.restdoc.core.parse.impl.JavaTypeInspector;
import cn.willingxyz.restdoc.core.parse.impl.RestDocParser;
import cn.willingxyz.restdoc.core.parse.impl.TypeNameParser;
import cn.willingxyz.restdoc.core.parse.utils.FormatUtils;
import cn.willingxyz.restdoc.core.parse.utils.ReflectUtils;
import cn.willingxyz.restdoc.core.parse.utils.TextUtils;
import cn.willingxyz.restdoc.core.utils.ClassNameUtils;
import cn.willingxyz.restdoc.swagger.common.PrimitiveSwaggerTypeInspector;
import cn.willingxyz.restdoc.swagger.common.SwaggerGeneratorConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import io.swagger.models.*;
import io.swagger.models.parameters.*;
import io.swagger.models.properties.*;
import lombok.var;

import javax.xml.validation.Schema;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class Swagger2RestDocGenerator implements IRestDocGenerator {

    public static void main(String[] args) {

        SwaggerGeneratorConfig generatorConfig = SwaggerGeneratorConfig.builder()
                .servers(Arrays.asList(SwaggerGeneratorConfig.ServerInfo.builder().description("server").url("/")
                        .build()))
                .swaggerTypeInspector(new PrimitiveSwaggerTypeInspector())
                .typeInspector(new JavaTypeInspector())
                .typeNameParser(new TypeNameParser())
                .build();
        var generator = new Swagger2RestDocGenerator(generatorConfig);

        var parseConfig = new RestDocParseConfig();
        parseConfig.setRestDocGenerator(generator);

        var parser = new RestDocParser(parseConfig);

        System.out.println(parser.parse());
    }

    private final SwaggerGeneratorConfig _config;

    public Swagger2RestDocGenerator(SwaggerGeneratorConfig config) {
        _config = config;
    }

    @Override
    public String generate(RootModel rootModel) {
        var swagger = generateSwagger(rootModel);

        var objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            var swaggerJson = objectMapper.writeValueAsString(swagger);
            return swaggerJson;
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
            tag.setName(_config.getTypeNameParser().parse(controller.getControllerClass()));
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
        if (_config.getServers() != null && _config.getServers().size() > 0) {
            swagger.setHost(_config.getServers().get(0).getUrl());
        }
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
        var path = new Path();

        var operation = new Operation();
        operation.addTag(_config.getTypeNameParser().parse(controller.getControllerClass()));
        operation.setSummary(TextUtils.getFirstLine(method.getDescription()));
        operation.setDescription(method.getDescription());
        operation.setDeprecated(method.getDeprecated());

        // 参数解析
        for (var param : method.getParameters()) {
            if (param.getLocation() == ParameterModel.ParameterLocation.QUERY) {
                convertQueryString(param, swagger).forEach(o -> operation.addParameter(o));
            }
            else if (param.getLocation() == ParameterModel.ParameterLocation.BODY) {
                operation.addParameter(convertRequestBody(param, swagger));
            }
            else if (param.getLocation() == ParameterModel.ParameterLocation.PATH) {
                operation.addParameter(generateSingleParameterSchema("path", param, swagger));
            }
            else if (param.getLocation() == ParameterModel.ParameterLocation.HEADER) {
                operation.addParameter(generateSingleParameterSchema("header", param, swagger));
            }
            else if (param.getLocation() == ParameterModel.ParameterLocation.FILE) {
                operation.addParameter(convertFileParameter(param));
            }
        }
        // 响应解析
        operation.setResponses(convertResponses(method, swagger));

        setHttpMethod(mapping, path, operation);

        for (var pathItem : mapping.getPaths()) {
            swagger.path(pathItem, path);
        }
    }

    private Map<String, Response> convertResponses(PathModel method, Swagger swagger) {
        var responses = new HashMap<String, Response>();
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

        apiResponse.setResponseSchema(getOrGenerateModel(returnModel.getDescription(), returnModel.getReturnType(), returnModel.getChildren(), swagger));
        apiResponse.setSchema(generateProperty(returnModel.getDescription(), returnModel.getReturnType(), returnModel.getChildren(), swagger));
        return apiResponse;
    }

    private Parameter convertFileParameter(ParameterModel param) {
        var requestBody = new FormParameter();

        requestBody.setDescription(param.getDescription());
        requestBody.setRequired(param.isRequired());

        return requestBody;
    }

    private Parameter convertRequestBody(ParameterModel parameterModel, Swagger swagger) {
        BodyParameter bodyParameter = new BodyParameter();
        bodyParameter.setDescription(parameterModel.getDescription());
        bodyParameter.setRequired(parameterModel.isRequired());

        Model model = getOrGenerateModel(parameterModel.getDescription(), parameterModel.getParameterType(), parameterModel.getChildren(), swagger);
        bodyParameter.setSchema(model);

        return bodyParameter;
    }

    private Model getOrGenerateModel(String description, Type parameterType, List<PropertyModel> children, Swagger swagger) {
        var componentName = ClassNameUtils.getComponentName(_config.getTypeInspector(), _config.getTypeNameParser(), parameterType);

        if (swagger.getDefinitions() == null)
            swagger.setDefinitions(new HashMap<>());
        if (!swagger.getDefinitions().containsKey(componentName))
        {
            Property property = generateProperty(description, parameterType, children, swagger);
            Model model = null;
            if (property instanceof ObjectProperty) {
                model = new ModelImpl();
                model.setProperties(((ObjectProperty) property).getProperties());
            }
            else if (property instanceof ArrayProperty)
            {
                ArrayModel arrayModel = new ArrayModel();
                arrayModel.setItems(property);
                model = arrayModel;
            }
            else
            {
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
            convertParameterChildren(paramModel.getChildren(), null, swagger)
                    .stream().forEach(o -> parameters.add(o));
        } else {
            parameters.add(generateSingleParameterSchema("query", paramModel, swagger));
        }
        return parameters;
    }

    private List<Parameter> convertParameterChildren(List<PropertyModel> propertyModels, String paraName,Swagger swagger) {
        var parameters = new ArrayList<Parameter>();
        String name = "";
        if (paraName != null)
            name = paraName + ".";
        for (var child : propertyModels) {
            if (child.isArray()) {
                convertParameterChildren(child.getChildren(), child.getName(), swagger).stream().forEach(o -> parameters.add(o));
            } else if (child.getChildren() == null || child.getChildren().size() == 0) {
                var parameter = new GenericParameter();

                parameter.setName(name + child.getName());
                parameter.setDescription(child.getDescription());
                parameter.setIn("query");

                parameter.setProperty(generateProperty(child.getDescription(), child.getPropertyType(), child.getChildren(), swagger));
                parameters.add(parameter);
            } else {
                parameters.addAll(convertParameterChildren(child.getChildren(), name + child.getName(), swagger));
            }
            // todo path
        }
        return parameters;
    }

    private Parameter generateSingleParameterSchema(String in, ParameterModel paramModel, Swagger swagger) {
        GenericParameter parameter = new GenericParameter();
        parameter.setIn(in);
        parameter.setName(paramModel.getName());
        parameter.setDescription(paramModel.getDescription());

        parameter.setProperty(generateProperty(paramModel.getDescription(), paramModel.getParameterType(), paramModel.getChildren(), swagger));

        return parameter;
    }

//    private void addSimpleQueryParameterSchema(ParameterModel paramModel, ArrayList<Parameter> parameters) {
//        var parameter = new QueryParameter();
//        parameter.setName(paramModel.getName());
//        parameter.setDescription(paramModel.getDescription());
//
//        parameter.setType(_config.getSwaggerTypeInspector().toSwaggerType(paramModel.getParameterType()));
//        parameter.setFormat(_config.getSwaggerTypeInspector().toSwaggerFormat(paramModel.getParameterType()));
//
//        if (paramModel.isRequired()) {
//            parameter.setRequired(true);
//        }
//        parameters.add(parameter);
//    }

//    private String putSchemaComponent(Type type, List<PropertyModel> children, Swagger swagger) {
//        var className = ClassNameUtils.getComponentName(_config.getTypeInspector(), _config.getTypeNameParser(), type);
//
//        if (swagger.getDefinitions() == null)
//            swagger.setDefinitions(new HashMap<>());
//        if (swagger.getDefinitions().containsKey(className)) {
//            return className;
//        }
//        var schema = generateModel(type, children, swagger);
//
//        swagger.getDefinitions().put(className, schema);
//
//        return className;
//    }



//    private Model generateModel(Type type, List<PropertyModel> children, Swagger swagger) {
//        if (_config.getTypeInspector().isCollection(type)) {
//            var arraySchema = new ArrayModel();
//            arraySchema.setItems(generateProperty(_config.getTypeInspector().getCollectionComponentType(type), children, swagger));
//            return arraySchema;
//        }
//        if (ReflectUtils.isEnum(type)) {
//            return generateEnumModel((Class) type);
//        }
//        var schema = new ModelImpl();
//        var className = _config.getTypeNameParser().parse(type);
//        schema.setName(className);
//        schema.setType(_config.getSwaggerTypeInspector().toSwaggerType(type));
//
//        var classDoc = RuntimeJavadoc.getJavadoc(type.getTypeName());
//        schema.setDescription(FormatUtils.format(classDoc.getComment()));
//
//        schema.setProperties(generateProperties(children, swagger));
//
//        return schema;
//    }

    private Property generateProperty(String description, Type type, List<PropertyModel> children, Swagger swagger) {
        // 数组/集合
        if (_config.getTypeInspector().isCollection(type)) {
            return generateArrayProperty(description, type, children, swagger);
        }
        // 枚举
        if (ReflectUtils.isEnum(type)) {
            return generateEnumProperty((Class) type);
        }
        // 简单类型
        if (children == null || children.isEmpty()) {
            return generateSimpleTypeSchema(description, type);
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
        var schemas = new HashMap<String, Property>();

        for (var propertyModel : propertyModels) {
            var schema = generateProperty(propertyModel.getDescription(), propertyModel.getPropertyType(), propertyModel.getChildren(), swagger);
            schemas.put(propertyModel.getName(), schema);
        }
        return schemas;
    }

    private Property generateSimpleTypeSchema(String description, Type type) {
        var property = new ComposedProperty();
        property.setDescription(description);
        property.setType(_config.getSwaggerTypeInspector().toSwaggerType(type));
        property.setFormat(_config.getSwaggerTypeInspector().toSwaggerFormat(type));
        return property;
    }

    private Property generateArrayProperty(String description, Type type, List<PropertyModel> children, Swagger swagger) {
        ArrayProperty arrayProperty = new ArrayProperty();
        arrayProperty.setDescription(description);
        arrayProperty.setItems(generateProperty(description, _config.getTypeInspector().getCollectionComponentType(type), children, swagger));
        return arrayProperty;
    }

    private Property generateEnumProperty(Class clazz) {
        var property = new StringProperty();
        var enumDoc = RuntimeJavadoc.getJavadoc(clazz);
        property.setDescription(FormatUtils.format(enumDoc.getComment()));
        var enums = Arrays.stream(clazz.getEnumConstants()).map(o -> o.toString()).collect(Collectors.toList());
        // todo 如何决定是string还是int
        property.setEnum(enums);
        if (enums.size() > 0) {
            property.setDefault(enums.get(0));
        }
        return property;
    }

//    private Map<String, Property> generateProperties(List<PropertyModel> children, Swagger swagger) {
//        var schemas = new HashMap<String, Property>();
//
//        for (var propertyModel : children) {
//            Property property = null;
//            if (_config.getTypeInspector().isCollection(propertyModel.getPropertyType())) {
//                var arraySchema = new ArrayProperty();
//                arraySchema.setItems(generateProperty(_config.getTypeInspector().getCollectionComponentType(propertyModel.getPropertyType()), propertyModel.getChildren(), swagger));
//                property = arraySchema;
//            } else if (ReflectUtils.isEnum(propertyModel.getPropertyType())) {
//                property = generateEnumProperty((Class) propertyModel.getPropertyType());
//            } else if (propertyModel.getChildren() == null || propertyModel.getChildren().size() == 0) {
//                var composedProperty = new ComposedProperty();
////                schema.setTitle(parameterModel.getPropertyType().getCanonicalName());
//                composedProperty.setDescription(propertyModel.getDescription());
//
//                composedProperty.setType(_config.getSwaggerTypeInspector().toSwaggerType(propertyModel.getPropertyType()));
//                composedProperty.setFormat(_config.getSwaggerTypeInspector().toSwaggerFormat(propertyModel.getPropertyType()));
//
//                if (propertyModel.getRequired() != null && propertyModel.getRequired()) {
//                    composedProperty.setRequired(true);
//                }
//                property = composedProperty;
//            } else {
//                property = generateProperty(propertyModel.getPropertyType(), propertyModel.getChildren(), swagger);
//            }
//            schemas.put(propertyModel.getName(), property);
//        }
//        return schemas;
//    }
//
//    private Model generateEnumModel(Class clazz) {
//        var arraySchema = new ModelImpl();
//        arraySchema.setType("string");
//        var enumDoc = RuntimeJavadoc.getJavadoc(clazz);
//        arraySchema.setDescription(FormatUtils.format(enumDoc.getComment()));
//        var enums = Arrays.stream(clazz.getEnumConstants()).map(o -> o.toString()).collect(Collectors.toList());
//        // todo 如何决定是string还是int
//        arraySchema.setEnum(enums);
//        if (enums.size() > 0) {
//            arraySchema.setDefaultValue(enums.get(0));
//        }
//        return arraySchema;
//    }


}
