package cn.willingxyz.restdoc.core.parse;

import cn.willingxyz.restdoc.core.parse.impl.JavaTypeInspector;
import cn.willingxyz.restdoc.core.parse.impl.JavadocControllerParser;
import cn.willingxyz.restdoc.core.parse.impl.JavadocMethodParser;
import cn.willingxyz.restdoc.core.parse.impl.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public  class RestDocParseConfig {
    private List<IControllerResolver> _controllerResolvers = new ArrayList<>();
    private List<IMethodResolver> _methodResolvers = new ArrayList<>();
    private List<IMethodParameterResolver> _methodParameterResolvers = new ArrayList<>();

    private List<IControllerParser> _controllerParsers = new ArrayList<>();
    private List<IMethodParser> _methodParsers = new ArrayList<>();
    private List<IMethodParameterParser> _methodParameterParsers = new ArrayList<>();
    private List<IMethodReturnParser> _returnParsers = new ArrayList<>();

    private ITypeInspector _typeInspector;

    private String _fieldPrefix;

    private IRestDocGenerator _restDocGenerator;

    public RestDocParseConfig()
    {
        _controllerParsers.add(new JavadocControllerParser());
        _methodParsers.add(new JavadocMethodParser());
//        _returnParsers.add(new MethodReturnParser(this));
        _typeInspector = new JavaTypeInspector();
    }
}
