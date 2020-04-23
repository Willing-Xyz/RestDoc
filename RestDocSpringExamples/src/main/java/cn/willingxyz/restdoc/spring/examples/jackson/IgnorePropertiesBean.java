package cn.willingxyz.restdoc.spring.examples.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(value = {"name", "age"})
@Data
public class IgnorePropertiesBean {
    private String _name;
    private String _age;
    @JsonIgnoreProperties(value = "address")
    private String _address;
    private String _extra;

    @JsonIgnoreProperties(value = "home")
    public String getHome() {
        return null;
    }

    @JsonIgnoreProperties(value = "dog")
    public void setDog(String dog) {}

    private FamilyExt _familyExt;

    @Data
    @JsonIgnoreProperties({"parent"})
    public static class Family {
        private String _brother;
        @JsonIgnoreProperties(value = "sister")
        private String _sister;
        private String _parent;
        private String _child;
    }

    @Data
    @JsonIgnoreProperties(value = { "brother"})
    public static class FamilyExt<T> extends Family {
        private String _ext;
        private T _obj;
    }
}
