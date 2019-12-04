package cn.willingxyz.restdoc.spring.examples.jackson;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

@Data
public class JacksonBean {
    /**
     * "@JsonGetter("jsonGetterAnno")"
     */
    @JsonGetter("jsonGetterAnno")
    public String getJsonGetter() {
        return null;
    }
    /**
     * "@JsonGetter("")"
     */
    @JsonGetter("")
    public String getJsonGetterDefaultValue() {
        return null;
    }
    /**
     * "@JsonSetter("jsonSetterAnno")"
     */
    @JsonSetter("jsonSetterAnno")
    public void setJsonSetter(String value) {}

    /**
     * "@JsonSetter("")"
     */
    @JsonSetter("")
    public void setJsonSetterDefaultValue(String value) {}

    /**
     * @JsonProperty(value = "jsonPropertyAnnoReadOnly", access = JsonProperty.Access.READ_ONLY)
     */
    @JsonProperty(value = "jsonPropertyAnnoReadOnly", access = JsonProperty.Access.READ_ONLY)
    private String _jsonPropertyReadOnly;
    /**
     * @JsonProperty(value = "jsonPropertyAnnoWriteOnly", access = JsonProperty.Access.WRITE_ONLY)
     */
    @JsonProperty(value = "jsonPropertyAnnoWriteOnly", access = JsonProperty.Access.WRITE_ONLY)
    private String _jsonPropertyWriteOnly;

    @JsonIgnore
    private String _jsonIgnore;
    @JsonIgnore(false)
    private String _jsonIgnoreFlase;

    private IgnoreData _ignoreData;

    @Data
    @JsonIgnoreType
    public static class IgnoreData {
        private String _ignoreField1;
        private String _ignoreField2;
    }
}
