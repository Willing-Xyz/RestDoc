package com.willing.springswagger.parse.impl;

import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.willing.springswagger.models.PathModel;
import com.willing.springswagger.parse.IMethodParser;
import lombok.var;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

public class SpringMethodParser implements IMethodParser {
    @Override
    public PathModel parse(Method method, MethodJavadoc methodJavadoc, PathModel pathModel) {

        var controllerRequestMapping = method.getDeclaringClass().getAnnotation(RequestMapping.class);
        String[] controllerPaths = null;
        RequestMethod[] controllerRequestMethods = null;
        if (controllerRequestMapping != null)
        {
            controllerPaths = controllerRequestMapping.path();
            controllerRequestMethods = controllerRequestMapping.method();
        }
        var controllerDeprecated = method.getDeclaringClass().getAnnotation(Deprecated.class);
        if (controllerDeprecated != null)
        {
            pathModel.setDeprecated(true);
        }
        var annotations = method.getAnnotations();
        for (var annotation : annotations) {
            var annotationType = annotation.annotationType();
            if (annotationType == RequestMapping.class) {
                var requestMappingAnno = (RequestMapping) annotation;
                pathModel.setHttpMethods(combineHttpMethod(requestMappingAnno.method(), controllerRequestMethods));
                pathModel.setPaths(combinePath(requestMappingAnno.path(), requestMappingAnno.value(), controllerPaths));
            } else if (annotationType == GetMapping.class) {
                var getMappingAnno = (GetMapping) annotation;
                pathModel.setHttpMethods(combineHttpMethod(new RequestMethod[]{RequestMethod.GET}, controllerRequestMethods));
                pathModel.setPaths(combinePath(getMappingAnno.path(), getMappingAnno.value(), controllerPaths));
            } else if (annotationType == PostMapping.class) {
                var postMappingAnno = (PostMapping) annotation;
                pathModel.setHttpMethods(combineHttpMethod(new RequestMethod[]{RequestMethod.POST}, controllerRequestMethods));
                pathModel.setPaths(combinePath(postMappingAnno.path(), postMappingAnno.value(), controllerPaths));
            } else if (annotationType == PutMapping.class) {
                var putMappingAnno = (PutMapping) annotation;
                pathModel.setHttpMethods(combineHttpMethod(new RequestMethod[]{RequestMethod.PUT}, controllerRequestMethods));
                pathModel.setPaths(combinePath(putMappingAnno.path(), putMappingAnno.value(), controllerPaths));
            } else if (annotationType == DeleteMapping.class) {
                var deleteMappingAnno = (DeleteMapping) annotation;
                pathModel.setHttpMethods(combineHttpMethod(new RequestMethod[]{RequestMethod.DELETE}, controllerRequestMethods));
                pathModel.setPaths(combinePath(deleteMappingAnno.path(), deleteMappingAnno.value(), controllerPaths));
            }else if (annotationType == Deprecated.class)
            {
                pathModel.setDeprecated(true);
            }
        }
        return pathModel;
    }

    private RequestMethod[] combineHttpMethod(RequestMethod[] method, RequestMethod[] controllerRequestMethods) {
        var set = new HashSet<RequestMethod>();
        if (method != null)
        {
            for (var m : method)
            {
                set.add(m);
            }
        }
        if (controllerRequestMethods != null)
        {
            for (var m : controllerRequestMethods)
            {
                set.add(m);
            }
        }
        return set.toArray(new RequestMethod[]{});
    }

    private String[] combinePath(String[] path, String[] values, String[] controllerPaths) {
        Set<String> paths = new HashSet<>();
        if (controllerPaths == null || controllerPaths.length == 0) {
            Arrays.stream(values).forEach(o -> paths.add(o));
            Arrays.stream(path).forEach(o -> paths.add(o));
            return paths.toArray(new String[]{});
        }
        for (var controllerPath : controllerPaths)
        {
            for (var methodPath : path)
            {
                paths.add(combineUriPath(controllerPath, methodPath));
            }
            for (var methodPath : values)
            {
                paths.add(combineUriPath(controllerPath, methodPath));
            }
        }
        return paths.toArray(new String[]{});
    }

    private String combineUriPath(String controllerPath, String methodPath) {
        return controllerPath + methodPath; // todo
    }
}
