package cn.willingxyz.restdoc.swagger.common.utils;

import cn.willingxyz.restdoc.core.parse.utils.EnumSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JsonUtils {
    public static ObjectMapper objectMapper()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Enum.class, new EnumSerializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
