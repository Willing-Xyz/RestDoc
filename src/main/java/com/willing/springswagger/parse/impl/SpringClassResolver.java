package com.willing.springswagger.parse.impl;

import com.willing.springswagger.models.ControllerModel;
import com.willing.springswagger.parse.IClassParser;
import com.willing.springswagger.parse.IClassResolver;
import lombok.var;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

public class SpringClassResolver implements IClassResolver {
    private List<String> _packages;

    public SpringClassResolver(List<String> packages)
    {
        _packages = packages;
    }

    @Override
    public List<Class> getClasses() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

        var classes = new ArrayList<Class>();
        for (var packageName : _packages) {
            var beans = scanner.findCandidateComponents(packageName);
            for (var bean : beans) {
                try {
                    var className = bean.getBeanClassName();
                    Class clazz = Class.forName(className);
                    classes.add(clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    // todo log
                }
            }
        }
        return classes;
    }
}
