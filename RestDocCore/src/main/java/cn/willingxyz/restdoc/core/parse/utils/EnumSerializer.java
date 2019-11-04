package cn.willingxyz.restdoc.core.parse.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.lang.reflect.Field;

public class EnumSerializer extends StdSerializer<Enum> {
    protected EnumSerializer(Class<Enum> t) {
        super(t);
    }

    public EnumSerializer() {
        super((Class<Enum>) null);
    }

    @Override
    public void serialize(Enum anEnum, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        try {
            Field field = anEnum.getClass().getDeclaredField("value");
            field.setAccessible(true);
            Object value = field.get(anEnum);
            jsonGenerator.writeString(value.toString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            jsonGenerator.writeString(anEnum.name());
        }
    }
}