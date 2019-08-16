package cn.willingxyz.restdoc.springswagger3;

import cn.willingxyz.restdoc.core.parse.IRestDocParser;
import org.springframework.beans.factory.annotation.Qualifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringSwagger3Controller {

    private SwaggerUIConfiguration _uiConfiguration;
    private IRestDocParser _docParser;
    private String _docCache;

    private ObjectMapper _objectMapper = new ObjectMapper();

    public SpringSwagger3Controller(IRestDocParser docParser, SwaggerUIConfiguration uiConfiguration)
    {
        _docParser = docParser;
        _uiConfiguration = uiConfiguration;
    }

    @GetMapping(value = {"/swagger.json", "/swagger3.json"})
    public String swaggerJson()
    {
//        if (_docCache != null)
//            return _docCache;
        _docCache = _docParser.parse();
        return _docCache;
    }

    @GetMapping("/swaggerUIConfiguration")
    public String swaggerUIConfiguration() throws JsonProcessingException {
        return _objectMapper.writeValueAsString(_uiConfiguration);
    }


}
