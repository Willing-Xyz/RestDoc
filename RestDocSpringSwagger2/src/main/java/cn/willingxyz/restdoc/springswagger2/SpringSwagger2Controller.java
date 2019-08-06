package cn.willingxyz.restdoc.springswagger2;

import cn.willingxyz.restdoc.core.parse.IRestDocParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringSwagger2Controller {

    private IRestDocParser _docParser;
    private String _docCache;

    public SpringSwagger2Controller(@Qualifier("swagger2") IRestDocParser docParser)
    {
        _docParser = docParser;
    }

    @GetMapping(value = "/swagger2.json")
    public String swaggerJson()
    {
//        if (_docCache != null)
//            return _docCache;
        _docCache = _docParser.parse();
        return _docCache;
    }
}
