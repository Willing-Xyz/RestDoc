package com.willing.springswagger.parse.impl;

import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.willing.springswagger.models.MappingModel;
import com.willing.springswagger.models.PathModel;
import com.willing.springswagger.parse.IMethodParser;
import lombok.var;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

public class SpringMethodParser implements IMethodParser {
    @Override
    public PathModel parse(Method method, MethodJavadoc methodJavadoc, PathModel pathModel) {

        setDeprecated(method, pathModel);

        var controllerRequestMapping = method.getDeclaringClass().getAnnotation(RequestMapping.class);
        String[] controllerPaths = null;
        RequestMethod[] controllerRequestMethods = null;
        if (controllerRequestMapping != null) {
            controllerPaths = combinePath(controllerRequestMapping.path(), controllerRequestMapping.value());
            controllerRequestMethods = controllerRequestMapping.method();
        }
        var annotations = method.getAnnotations();
        for (var annotation : annotations) {

            RequestMethod[] methods = null;
            String[] mappingPaths = null;
            String[] mappingValues = null;

            var annotationType = annotation.annotationType();
            if (annotationType == RequestMapping.class) {
                var requestMappingAnno = (RequestMapping) annotation;
                methods = requestMappingAnno.method();
                mappingPaths = requestMappingAnno.path();
                mappingValues = requestMappingAnno.value();
            } else if (annotationType == GetMapping.class) {
                var getMappingAnno = (GetMapping) annotation;
                methods = new RequestMethod[]{RequestMethod.GET};
                mappingPaths = getMappingAnno.path();
                mappingValues = getMappingAnno.value();
            } else if (annotationType == PostMapping.class) {
                var postMappingAnno = (PostMapping) annotation;
                methods = new RequestMethod[]{RequestMethod.POST};
                mappingPaths = postMappingAnno.path();
                mappingValues = postMappingAnno.value();
            } else if (annotationType == PutMapping.class) {
                var putMappingAnno = (PutMapping) annotation;
                methods = new RequestMethod[]{RequestMethod.PUT};
                mappingPaths = putMappingAnno.path();
                mappingValues = putMappingAnno.value();
            } else if (annotationType == DeleteMapping.class) {
                var deleteMappingAnno = (DeleteMapping) annotation;
                methods = new RequestMethod[]{RequestMethod.DELETE};
                mappingPaths = deleteMappingAnno.path();
                mappingValues = deleteMappingAnno.value();
            }
            else if (annotationType == PatchMapping.class)
            {
                var patchMappingAnno = (PatchMapping) annotation;
                methods = new RequestMethod[]{RequestMethod.PATCH};
                mappingPaths = patchMappingAnno.path();
                mappingValues = patchMappingAnno.value();
            }
            var mapping = new MappingModel();
            mapping.setHttpMethods(combineHttpMethod(methods, controllerRequestMethods));
            mapping.setPaths(combineControlerPath(combinePath(mappingPaths, mappingValues), controllerPaths));
            pathModel.getMappings().add(mapping);
        }
        return pathModel;
    }

    private void setDeprecated(Method method, PathModel pathModel) {
        var deprecatedAnno = method.getDeclaringClass().getAnnotation(Deprecated.class);
        if (deprecatedAnno != null) {
            pathModel.setDeprecated(true);
        }
        else
        {
            deprecatedAnno = method.getAnnotation(Deprecated.class);
            if (deprecatedAnno != null)
            {
                pathModel.setDeprecated(true);
            }
        }
    }

    private RequestMethod[] combineHttpMethod(RequestMethod[] methodA, RequestMethod[] methodB) {
        var set = new HashSet<RequestMethod>();
        if (methodA != null) {
            for (var m : methodA) {
                set.add(m);
            }
        }
        if (methodB != null) {
            for (var m : methodB) {
                set.add(m);
            }
        }
        return set.toArray(new RequestMethod[]{});
    }

    private String[] combineControlerPath(String[] path, String[] controllerPaths) {
        Set<String> paths = new HashSet<>();
        if (controllerPaths == null || controllerPaths.length == 0) {
            if (path != null)
                Arrays.stream(path).forEach(o -> paths.add(o));
            return paths.toArray(new String[]{});
        }
        for (var controllerPath : controllerPaths) {
            for (var methodPath : path) {
                paths.add(combineUriPath(controllerPath, methodPath));
            }
        }
        return paths.toArray(new String[]{});
    }

    private String[] combinePath(String[] path, String[] values)
    {
        Set<String> paths = new HashSet<>();
        if (path != null)
            Arrays.stream(values).forEach(o -> paths.add(o));
        if (values != null)
            Arrays.stream(path).forEach(o -> paths.add(o));
        return paths.toArray(new String[]{});
    }

    private String combineUriPath(String controllerPath, String methodPath) {
        if (controllerPath.length() > 0 && controllerPath.charAt(0) != '/')
            controllerPath = "/" + controllerPath;
        if (controllerPath.length() > 0 && controllerPath.charAt(controllerPath.length() - 1) != '/')
            controllerPath = controllerPath + "/";
        if (methodPath.length() > 0 && methodPath.charAt(0) == '/')
            methodPath = methodPath.substring(1);
        return controllerPath + methodPath;
    }
}
