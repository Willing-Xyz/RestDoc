package cn.willingxyz.restdoc.springswagger3;

import cn.willingxyz.restdoc.core.parse.IRestDocParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringSwaggerController {

    private IRestDocParser _docParser;
    private String _docCache;

    public SpringSwaggerController(IRestDocParser docParser)
    {
        _docParser = docParser;
    }

    @GetMapping("/swagger.json")
    public String swaggerJson()
    {
//        if (_docCache != null)
//            return _docCache;
        _docCache = _docParser.parse();
        return _docCache;
    }
}
