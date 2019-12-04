package cn.willingxyz.restdoc.spring;

import cn.willingxyz.restdoc.core.config.RestDocParseConfig;
import cn.willingxyz.restdoc.core.parse.impl.IgnoreApiControllerFilter;
import cn.willingxyz.restdoc.core.parse.impl.IgnoreApiMethodFilter;
import cn.willingxyz.restdoc.spring.parameter.parser.*;
import cn.willingxyz.restdoc.spring.parameter.filter.SpringAnnotationParameterFilter;
import cn.willingxyz.restdoc.spring.parameter.filter.SpringClassParameterFilter;
import cn.willingxyz.restdoc.spring.response.SpringResponseBodyReturnParser;

public class SpringRestDocParseConfig extends RestDocParseConfig {

    public SpringRestDocParseConfig() {

    }
}
