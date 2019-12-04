package cn.willingxyz.restdoc.spring;

import com.github.therapi.runtimejavadoc.MethodJavadoc;
import cn.willingxyz.restdoc.core.models.MappingModel;
import cn.willingxyz.restdoc.core.models.PathModel;
import cn.willingxyz.restdoc.core.parse.IMethodParser;
import com.google.auto.service.AutoService;
import lombok.var;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@AutoService(IMethodParser.class)
public class SpringMethodParser implements IMethodParser {
    @Override
    public PathModel parse(Method method, MethodJavadoc methodJavadoc, PathModel pathModel) {

        setDeprecated(method, pathModel);

        var controllerRequestMapping = method.getDeclaringClass().getAnnotation(RequestMapping.class);
        String[] controllerPaths = new String[]{"/"};
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
                if (requestMappingAnno.method() == null || requestMappingAnno.method().length == 0) {
                    methods = new RequestMethod[]{GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD};
                }
                else
                {
                    methods = requestMappingAnno.method();
                }
                mappingPaths = requestMappingAnno.path();
                mappingValues = requestMappingAnno.value();
            } else if (annotationType == GetMapping.class) {
                var getMappingAnno = (GetMapping) annotation;
                methods = new RequestMethod[]{GET};
                mappingPaths = getMappingAnno.path();
                mappingValues = getMappingAnno.value();
            } else if (annotationType == PostMapping.class) {
                var postMappingAnno = (PostMapping) annotation;
                methods = new RequestMethod[]{POST};
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
            mapping.setPaths(combineControllerPath(combinePath(mappingPaths, mappingValues), controllerPaths));
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

    private MappingModel.HttpMethod[] combineHttpMethod(RequestMethod[] methodA, RequestMethod[] methodB) {
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
        var list = new ArrayList<MappingModel.HttpMethod>();
        for (var s : set)
        {
            MappingModel.HttpMethod t = null;
            switch (s)
            {
                case GET:
                    t = MappingModel.HttpMethod.GET;
                    break;
                case POST:
                    t = MappingModel.HttpMethod.POST;
                    break;
                case PUT:
                    t = MappingModel.HttpMethod.PUT;
                    break;
                case DELETE:
                    t = MappingModel.HttpMethod.DELETE;
                    break;
                case HEAD:
                    t = MappingModel.HttpMethod.HEAD;
                    break;
                case PATCH:
                    t = MappingModel.HttpMethod.PATCH;
                    break;
                case OPTIONS:
                    t = MappingModel.HttpMethod.OPTIONS;
                    break;
                case TRACE:
                    t = MappingModel.HttpMethod.TRACE;
                    break;
            }
            list.add(t);
        }
        return list.toArray(new MappingModel.HttpMethod[]{});
    }

    private String[] combineControllerPath(String[] path, String[] controllerPaths) {
        Set<String> paths = new HashSet<>();
        if (controllerPaths == null || controllerPaths.length == 0) {
            if (path != null && path.length > 0)
                Arrays.stream(path).forEach(o -> paths.add(normalize(o)));
        }
        if (controllerPaths != null && controllerPaths.length > 0)
        {
            if (path == null || path.length == 0)
                Arrays.stream(controllerPaths).forEach(o -> paths.add(normalize(o)));
        }
        if (controllerPaths == null || controllerPaths.length == 0)
        {
            if (path == null || path.length == 0)
                paths.add("/");
        }
        for (var controllerPath : controllerPaths) {
            for (var methodPath : path) {
                paths.add(normalize(combineUriPath(controllerPath, methodPath)));
            }
        }
        return paths.toArray(new String[]{});
    }

    private String normalize(String path) {
        if (path == null) return path;
        if (path.isEmpty()) return "/";
        if (!path.startsWith("/")) return "/" + path;
        return path;
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
