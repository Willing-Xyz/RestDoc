package com.willing.springswagger.parse.impl;

import com.github.therapi.runtimejavadoc.*;
import com.willing.springswagger.models.*;
import com.willing.springswagger.parse.DocParseConfiguration;
import com.willing.springswagger.parse.IDocParser;
import lombok.var;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DocParser implements IDocParser {
    private final DocParseConfiguration _configuration;
    private final CommentFormatter _formatter = new CommentFormatter();

    public DocParser(DocParseConfiguration configuration)
    {
        _configuration = configuration;
    }

    @Override
    public RootModel parse() {
//        var scanner = new ClassPathScanningCandidateComponentProvider(false);
//        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
//
//        var rootModel = new RootModel();
//        for (var packageName : _configuration.getPackages())
//        {
//            handlePackage(scanner, packageName, rootModel);
//        }
        var rootModel = new RootModel();
        for (var classResolver : _configuration.getClassResolvers()) {
            for (var clazz : classResolver.getClasses()) {
                var controllerModel = handleClass(clazz);
                rootModel.getControllers().add(controllerModel);
            }
        }
        return null;
    }

//    private void handlePackage(ClassPathScanningCandidateComponentProvider scanner, String packageName, RootModel rootModel) {
//        var beans = scanner.findCandidateComponents(packageName);
//        for (var bean : beans)
//        {
//            try {
//                var className = bean.getBeanClassName();
//                Class clazz = Class.forName(className);
//
//                handleClass(clazz, rootModel);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//                // todo log
//            }
//        }
//    }

    private ControllerModel handleClass(Class clazz) {
        var controllerModel = new ControllerModel();
        for (var classParser : _configuration.getClassParsers())
        {
            controllerModel = classParser.parse(clazz, controllerModel);
        }
        for (var method : clazz.getMethods())
        {
            var pathModel = handleMethod(method);
            controllerModel.getControllerMethods().add(pathModel);
        }
        return controllerModel;
//        var className = clazz.getCanonicalName();
//        ClassJavadoc classDoc = RuntimeJavadoc.getJavadoc(className);
//
//        controllerModel.setName(className);
//        controllerModel.setDescription(format(classDoc.getComment()));
//
//        for (var method : clazz.getMethods())
//        {
//            Optional<MethodJavadoc> methodDoc = classDoc.getMethods().stream().filter(o -> o.getName().equals(method.getName())).findFirst();
//            handleMethod(method, controllerModel.getControllerMethods(), classDoc);
//        }
//        return controllerModel;
    }

    private PathModel handleMethod(Method method) {
        var pathModel = new PathModel();
        for (var methodParser : _configuration.getMethodParsers())
        {
            pathModel = methodParser.parse(method, pathModel);
        }
        for (var parameter : method.getParameters())
        {
            var parameterModel = handleMethodParameter(parameter);
            pathModel.getParameters().add(parameterModel);
        }
        var responseModels = handleReturnValue(method.getReturnType());
        pathModel.setResponse(responseModels);
        return pathModel;
    }

    private List<ResponseModel> handleReturnValue(Class<?> returnType) {
        var responseModels = new ArrayList<ResponseModel>();
        ResponseModel lastResponseModel = null;
        for (var returnParser : _configuration.getReturnParsers())
        {
            if (returnParser.isNew())
            {
                var responseModel = returnParser.parse(returnType, new ResponseModel());
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
                lastResponseModel = returnParser.parse(returnType, lastResponseModel);
            }
        }
        return responseModels;
    }

    private ParameterModel handleMethodParameter(Parameter parameter) {
        var parameterModel = new ParameterModel();
        for (var parameterParser : _configuration.getMethodParameterParsers())
        {
            parameterModel = parameterParser.parse(parameter, parameterModel);
        }
        return parameterModel;
    }

//    private PathModel handleMethod(Method method, Optional<MethodJavadoc> methodDoc) {
//        PathModel pathModel = new PathModel();
//        if (methodDoc.isPresent())
//        {
//            pathModel.setDescription(format(methodDoc.get().getComment()));
//        }
//        for (var parameter : method.getParameters())
//        {
//            Optional<ParamJavadoc> paramJavadoc = Optional.empty();
//            if (methodDoc.isPresent())
//            {
//                methodDoc.get().getParams().stream().filter(o -> o.getName().equals(parameter.getName())).findFirst();
//            }
//            handleMethodParameter(parameter, paramJavadoc);
//        }
//        var annotations = method.getAnnotations();
//        for (var annotation : annotations) {
//            var annotationType = annotation.annotationType();
//            if (annotationType == RequestMapping.class) {
//                var requestMappingAnno = (RequestMapping) annotation;
//                pathModel.setHttpMethods(requestMappingAnno.method());
//                pathModel.setPaths(requestMappingAnno.path());
//            } else if (annotationType == GetMapping.class) {
//                var getMappingAnno = (GetMapping) annotation;
//                pathModel.setHttpMethods(new RequestMethod[]{RequestMethod.GET});
//                pathModel.setPaths(getMappingAnno.path());
//            } else if (annotationType == PostMapping.class) {
//                var postMappingAnno = (PostMapping) annotation;
//                pathModel.setHttpMethods(new RequestMethod[]{RequestMethod.POST});
//                pathModel.setPaths(postMappingAnno.path());
//            } else if (annotationType == PutMapping.class) {
//                var putMappingAnno = (PutMapping) annotation;
//                pathModel.setHttpMethods(new RequestMethod[]{RequestMethod.PUT});
//                pathModel.setPaths(putMappingAnno.path());
//            } else if (annotationType == DeleteMapping.class) {
//                var putMappingAnno = (DeleteMapping) annotation;
//                pathModel.setHttpMethods(new RequestMethod[]{RequestMethod.DELETE});
//                pathModel.setPaths(putMappingAnno.path());
//            }
//        }
//        return pathModel;
//    }

//    private ParameterModel handleMethodParameter(Parameter parameter, Optional<ParamJavadoc> paramJavadoc) {
//        if (shouldIgnoreParameter(parameter))
//            return null;
//        ParameterModel parameterModel = new ParameterModel();
//        parameterModel.setName(parameter.getName());
//        if (paramJavadoc.isPresent())
//        {
//            parameterModel.setDescription(format(paramJavadoc.get().getComment()));
//        }
//        var parameterClass = parameter.getType();
//        if (!isPrimitiveClass(parameterClass))
//        {
//
//        }
//    }

    private boolean isPrimitiveClass(Class<?> parameterClass) {
        return false;
    }

    private boolean shouldIgnoreParameter(Parameter parameter) {
        // 方法中的一些参数应该忽略，如HttpServletRequest
        return false;
    }

    private String format(Comment comment)
    {
        return _formatter.format(comment);
    }
}
