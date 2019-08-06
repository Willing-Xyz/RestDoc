package cn.willingxyz.restdoc.springswagger3;

import cn.willingxyz.restdoc.core.parse.IRestDocParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringSwagger3Controller {

    private IRestDocParser _docParser;
    private String _docCache;

    public SpringSwagger3Controller(@Qualifier("swagger3") IRestDocParser docParser)
    {
        _docParser = docParser;
    }

    @GetMapping(value = {"/swagger.json", "/swagger3.json"})
    public String swaggerJson()
    {
//        if (_docCache != null)
//            return _docCache;
        _docCache = _docParser.parse();
        return _docCache;
    }
}
