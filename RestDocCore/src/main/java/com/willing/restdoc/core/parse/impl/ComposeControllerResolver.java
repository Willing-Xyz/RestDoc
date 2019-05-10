package com.willing.restdoc.core.parse.impl;

import com.willing.restdoc.core.parse.IControllerResolver;
import lombok.var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//public class ComposeControllerResolver implements IControllerResolver {
//    private List<IControllerResolver> _controllerResolvers = new ArrayList<>();
//
//    public ComposeControllerResolver(IControllerResolver... args)
//    {
//        _controllerResolvers.addAll(Arrays.asList(args));
//    }
//
//    @Override
//    public List<Class> getClasses() {
//        var list = new ArrayList<Class>();
//        _controllerResolvers.forEach(o -> list.addAll(o.getClasses()));
//        return list;
//    }
//}
