package com.willing.springswagger.parse;

import com.willing.springswagger.parse.impl.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class DocParseConfiguration {
    private List<IClassResolver> _classResolvers;
    private List<IMethodResolver> _methodResolvers;
    private List<IMethodParameterResolver> _methodParameterResolvers;

    private List<IClassParser> _classParsers;
    private List<IMethodParser> _methodParsers;
    private List<IMethodParameterParser> _methodParameterParsers;
    private List<IMethodReturnParser> _returnParsers;
    private IDocGenerator _docGenerator;

    public DocParseConfiguration(List<String> packages)
    {
        _docGenerator = new Swagger3DocGenerator(new Swagger3Configuration.Swagger3ConfigurationBuilder()
                .description("todo description")
                .version("todo version")
                .title("todo title")
                .servers(Arrays.asList(new Swagger3Configuration.ServerInfo.ServerInfoBuilder().description("todo server descritpion").url("http://www.todo.com").build()))
                .build());

        _classResolvers = new ArrayList<>();
        _classResolvers.add(new SpringClassResolver(packages));

        _methodResolvers = new ArrayList<>();
        _methodResolvers.add(new SpringMethodResolver());

        _methodParameterResolvers = new ArrayList<>();
        _methodParameterResolvers.add(new SpringMethodParameterResolver());
        // ---------

        _classParsers = new ArrayList<>();
        _classParsers.add(new JavadocClassParser());

        _methodParsers = new ArrayList<>();
        _methodParsers.add(new JavadocMethodParser());
        _methodParsers.add(new SpringMethodParser());

        _methodParameterParsers = new ArrayList<>();
        _methodParameterParsers.add(new SpringMethodParameterParser());

        _returnParsers = new ArrayList<>();
        _returnParsers.add(new MethodReturnParser());
    }
}
