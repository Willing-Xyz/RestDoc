package com.willing.restdoc.core.parse.impl;

import com.github.therapi.runtimejavadoc.*;
import com.willing.restdoc.core.models.*;
import com.willing.restdoc.core.parse.DocParseConfiguration;
import com.willing.restdoc.core.parse.IDocParser;
import com.willing.restdoc.core.parse.utils.ReflectUtils;
import lombok.var;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DocParser implements IDocParser {
    private final DocParseConfiguration _configuration;

    public DocParser(DocParseConfiguration configuration)
    {
        _configuration = configuration;
    }

    @Override
    public String parse() {
        var rootModel = new RootModel();
        for (var classResolver : _configuration.getClassResolvers()) {
            for (var clazz : classResolver.getClasses()) {
                var controllerModel = handleClass(clazz);
                rootModel.getControllers().add(controllerModel);
            }
        }
        return _configuration.getDocGenerator().generate(rootModel);
    }

    private ControllerModel handleClass(Class clazz) {
        var controllerModel = new ControllerModel();
        ClassJavadoc classDoc = RuntimeJavadoc.getJavadoc(clazz.getCanonicalName());
        for (var classParser : _configuration.getClassParsers())
        {
            controllerModel = classParser.parse(clazz, classDoc, controllerModel);
        }
        for (var method : ReflectUtils.getAllMethods(clazz))
        {
            boolean isSupport = false;
            for (var methodResolver : _configuration.getMethodResolvers())
            {
                if (methodResolver.isSupport(method))
                    isSupport = true;
            }
            if (!isSupport)
                continue;
            Optional<MethodJavadoc> methodDoc = classDoc.getMethods().stream().filter(o -> o.getName().equals(method.getName())).findFirst();
            var pathModel = handleMethod(method, methodDoc.orElse(null));
            controllerModel.getControllerMethods().add(pathModel);
        }
        return controllerModel;
    }

    private PathModel handleMethod(Method method, MethodJavadoc methodJavadoc) {
        var pathModel = new PathModel();
        for (var methodParser : _configuration.getMethodParsers())
        {
            pathModel = methodParser.parse(method, methodJavadoc, pathModel);
        }
        for (var parameter : method.getParameters())
        {
            boolean isSupport = false;
            for (var methodParameterResolver : _configuration.getMethodParameterResolvers())
            {
                if (methodParameterResolver.isSupport(parameter))
                    isSupport = true;
            }
            if (!isSupport)
                continue;
            ParamJavadoc paramJavadoc = null;
            if (methodJavadoc != null) {
                paramJavadoc = methodJavadoc.getParams().stream().filter(o -> o.getName().equals(parameter.getName())).findFirst().orElse(null);
            }
            var parameterModel = handleMethodParameter(parameter, paramJavadoc);
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
        var parameterModel = new ParameterModel();
        for (var parameterParser : _configuration.getMethodParameterParsers())
        {
            parameterModel = parameterParser.parse(parameter, paramJavadoc, parameterModel);
        }
        return parameterModel;
    }
}
