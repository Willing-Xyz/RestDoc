package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.models.*;
import cn.willingxyz.restdoc.core.parse.IRestDocParser;
import cn.willingxyz.restdoc.core.config.RestDocParseConfig;
import cn.willingxyz.restdoc.core.parse.utils.ReflectUtils;
import cn.willingxyz.restdoc.core.parse.utils.RuntimeJavadocUtils;
import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.Comment;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.ParamJavadoc;
import lombok.var;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestDocParser implements IRestDocParser {
    private final RestDocParseConfig _configuration;
    private static final LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    public RestDocParser(RestDocParseConfig configuration) {
        _configuration = configuration;
    }

    @Override
    public String parse() {
        var rootModel = new RootModel();
        for (var controllerResolver : _configuration.getControllerResolvers()) {
            for (var clazz : controllerResolver.getClasses()) {
                if (!filterController(clazz))
                    continue;
                var controllerModel = handleController(clazz);
                rootModel.getControllers().add(controllerModel);
            }
        }
        return _configuration.getRestDocGenerator().generate(rootModel);
    }

    private boolean filterController(Class clazz) {
        for (var controllerFilter : _configuration.getControllerFilters()) {
            if (!controllerFilter.isSupport(clazz))
                return false;
        }
        return true;
    }

    private ControllerModel handleController(Class clazz) {
        var controllerModel = new ControllerModel();
        ClassJavadoc classDoc = RuntimeJavadocUtils.getClassJavadoc(clazz);
        for (var classParser : _configuration.getControllerParsers()) {
            classParser.parse(clazz, classDoc, controllerModel);
        }
        for (var method : ReflectUtils.getAllMethods(clazz)) {
            Optional<MethodJavadoc> methodDoc = RuntimeJavadocUtils.getAllMethodJavadoc(clazz).stream().filter(o -> o.getName().equals(method.getName())).findFirst();
            var pathModel = handleMethod(method, methodDoc.orElse(null));
            if (pathModel != null)
                controllerModel.getControllerMethods().add(pathModel);
        }
        return controllerModel;
    }

    private PathModel handleMethod(Method method, MethodJavadoc methodJavadoc) {
        for (var methodResolver : _configuration.getMethodFilters()) {
            if (!methodResolver.isSupport(method))
                return null;
        }


        var pathModel = new PathModel();
        for (var methodParser : _configuration.getMethodParsers()) {
            pathModel = methodParser.parse(method, methodJavadoc, pathModel);
        }
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            ParamJavadoc paramJavadoc = null;
            Parameter parameter = parameters[i];
            if (!parameter.isNamePresent() && parameterNames != null) {
                String parameterName = parameterNames[i];
                ReflectUtils.setFieldValue(parameter, "name", parameterName);
            }
            if (methodJavadoc != null) {
                paramJavadoc = methodJavadoc.getParams().stream().filter(o -> o.getName().equals(parameter.getName())).findFirst().orElse(null);
            }
            var parameterModel = handleMethodParameter(parameter, paramJavadoc);
            if (parameterModel != null)
                pathModel.getParameters().add(parameterModel);

        }
        Comment returnComment = null;
        if (methodJavadoc != null)
            returnComment = methodJavadoc.getReturns();
        var responseModels = handleReturnValue(method, returnComment);
        pathModel.setResponse(responseModels);
        return pathModel;
    }

    private List<ResponseModel> handleReturnValue(Method method, Comment returns) {
        var responseModels = new ArrayList<ResponseModel>();
        ResponseModel lastResponseModel = null;
        for (var returnParser : _configuration.getReturnParsers()) {
            if (returnParser.isNew()) {
                var responseModel = returnParser.parse(method, returns, new ResponseModel());
                lastResponseModel = responseModel;
                responseModels.add(responseModel);
            } else {
                if (lastResponseModel == null) {
                    lastResponseModel = new ResponseModel();
                    responseModels.add(lastResponseModel);
                }
                lastResponseModel = returnParser.parse(method, returns, lastResponseModel);
            }
        }
        for (int i = 0; i < responseModels.size(); ++i)
        {
            ResponseModel responseModel = responseModels.get(i);
            responseModel = _configuration.getResponsePostProcessor().postProcess(responseModel, method);
            responseModels.set(i, responseModel);
        }
        responseModels.remove(null);
        return responseModels;
    }

    private ParameterModel handleMethodParameter(Parameter parameter, ParamJavadoc paramJavadoc) {
        for (var methodParameterResolver : _configuration.getMethodParameterFilters()) {
            if (!methodParameterResolver.isSupport(parameter))
                return null;
        }
        var parameterModel = new ParameterModel();
        for (var parameterParser : _configuration.getMethodParameterParsers()) {
            if (parameterParser.isSupport(parameter)) {
                parameterModel = parameterParser.parse(parameter, paramJavadoc, parameterModel);

                parameterModel = _configuration.getParameterPostProcessor().postProcess(parameterModel, parameter);
                break;
            }
        }
        return parameterModel;
    }
}
