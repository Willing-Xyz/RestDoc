package cn.willingxyz.restdoc.spring;

import cn.willingxyz.restdoc.core.config.AbstractRestDocParseConfigAware;
import cn.willingxyz.restdoc.core.parse.IControllerResolver;
import com.google.auto.service.AutoService;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;

import java.util.*;

public class SpringControllerResolver implements IControllerResolver {
    private static Logger _logger = LoggerFactory.getLogger(SpringControllerResolver.class);
    private final List<String> _packages;

    public SpringControllerResolver(List<String> packages) {
        _packages = packages;
    }

    @Override
    public List<Class> getClasses() {
        List<String> packages = _packages;

        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));

        var classes = new ArrayList<Class>();
        if (packages == null) packages = Arrays.asList("cn", "com");
        for (var packageName : packages) {
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
