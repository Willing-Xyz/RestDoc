package com.willing.springswagger.controllers;

import com.willing.springswagger.parse.IDocParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringSwaggerController {

    private IDocParser _docParser;
    private String _docCache;

    public SpringSwaggerController(IDocParser docParser)
    {
        _docParser = docParser;
    }

    @GetMapping("/swagger.json")
    public String swaggerJson()
    {
        if (_docCache != null)
            return _docCache;
        _docCache = _docParser.parse();
        return _docCache;
    }
}
