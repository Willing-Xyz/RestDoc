package cn.willingxyz.restdoc.spring;

import cn.willingxyz.restdoc.core.parse.IControllerResolver;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

public class SpringControllerResolver implements IControllerResolver {
    private static Logger _logger = LoggerFactory.getLogger(SpringControllerResolver.class);

    private List<String> _packages;

    public SpringControllerResolver(List<String> packages)
    {
        _packages = packages;
    }

    @Override
    public List<Class> getClasses() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));

        var classes = new ArrayList<Class>();
        for (var packageName : _packages) {
            var beans = scanner.findCandidateComponents(packageName);
            for (var bean : beans) {
                try {
                    var className = bean.getBeanClassName();
                    Class clazz = Class.forName(className);
                    classes.add(clazz);
                } catch (ClassNotFoundException e) {
                    _logger.warn("not found class:" + bean.getBeanClassName(), e);
                }
            }
        }
        return classes;
    }
}
