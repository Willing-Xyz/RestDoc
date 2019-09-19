package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.models.*;
import com.github.therapi.runtimejavadoc.*;
import cn.willingxyz.restdoc.core.parse.RestDocParseConfig;
import cn.willingxyz.restdoc.core.parse.IRestDocParser;
import cn.willingxyz.restdoc.core.parse.utils.ReflectUtils;
import cn.willingxyz.restdoc.core.parse.utils.RuntimeJavadocUtils;
import lombok.var;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class RestDocParser implements IRestDocParser {
    private final RestDocParseConfig _configuration;

    public RestDocParser(RestDocParseConfig configuration)
    {
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
        for (var classParser : _configuration.getControllerParsers())
        {
            classParser.parse(clazz, classDoc, controllerModel);
        }
        for (var method : ReflectUtils.getAllMethods(clazz))
        {
            Optional<MethodJavadoc> methodDoc = RuntimeJavadocUtils.getAllMethodJavadoc(clazz).stream().filter(o -> o.getName().equals(method.getName())).findFirst();
            var pathModel = handleMethod(method, methodDoc.orElse(null));
            if (pathModel != null)
                controllerModel.getControllerMethods().add(pathModel);
        }
        return controllerModel;
    }

    private PathModel handleMethod(Method method, MethodJavadoc methodJavadoc) {
        for (var methodResolver : _configuration.getMethodResolvers())
        {
            if (!methodResolver.isSupport(method))
                return null;
        }


        var pathModel = new PathModel();
        for (var methodParser : _configuration.getMethodParsers())
        {
            pathModel = methodParser.parse(method, methodJavadoc, pathModel);
        }
        for (var parameter : method.getParameters())
        {
            ParamJavadoc paramJavadoc = null;
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
        for (var returnParser : _configuration.getReturnParsers())
        {
            if (returnParser.isNew())
            {
                var responseModel = returnParser.parse(method, returns, new ResponseModel());
                lastResponseModel = responseModel;
                responseModels.add(responseModel);
            }
            else
            {
                if (lastResponseModel == null)
                {
                    lastResponseModel = new ResponseModel();
                    responseModels.add(lastResponseModel);
                }
                lastResponseModel = returnParser.parse(method, returns, lastResponseModel);
            }
        }
        return responseModels;
    }

    private ParameterModel handleMethodParameter(Parameter parameter, ParamJavadoc paramJavadoc) {
        for (var methodParameterResolver : _configuration.getMethodParameterResolvers())
        {
            if (!methodParameterResolver.isSupport(parameter))
                return null;
        }
        var parameterModel = new ParameterModel();
        for (var parameterParser : _configuration.getMethodParameterParsers())
        {
            if (parameterParser.isSupport(parameter)) {
                parameterModel = parameterParser.parse(parameter, paramJavadoc, parameterModel);
                break;
            }
        }
        return parameterModel;
    }
}
