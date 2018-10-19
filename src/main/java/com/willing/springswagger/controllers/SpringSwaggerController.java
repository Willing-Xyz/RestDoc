package com.willing.springswagger.controllers;

import com.willing.springswagger.parse.IDocParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringSwaggerController {

    private IDocParser _docParser;

    public SpringSwaggerController(IDocParser docParser)
    {
        _docParser = docParser;
    }

    @GetMapping("/swagger.json")
    public String swaggerJson()
    {
        return _docParser.parse();
    }
}
