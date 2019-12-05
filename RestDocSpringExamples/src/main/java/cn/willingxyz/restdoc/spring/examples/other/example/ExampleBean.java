package cn.willingxyz.restdoc.spring.examples.other.example;

import cn.willingxyz.restdoc.core.annotations.Example;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExampleBean {
    /**
     * 指定example
     *
     * @example 🙂2019-01-01 12:12:23🙂
     */
    private LocalDateTime _localDateTime;
    /**
     * 指定example (通过注解）
     */
    @Example("🙂2019-10-01 22:22:32🙂")
    private LocalDateTime _localDateTimeByAnnotation;

    /**
     * @example 😊他明白😠
     */
    private String _example;
    @Example("field by annotation")
    private String _exampleByAnnotation;

    /**
     * @returnExample 🙂getName🙂
     */
    public String getName() {
        return null;
    }
    @Example("getNameByAnnotation")
    public String getNameByAnnotation() {
        return null;
    }
    /**
     * @paramExample 😠setTitle😠
     */
    public void setTitle(String title)
    {
    }

    public void setTitleByAnnotation(@Example("setTitleByAnnotation") String title)
    {
    }
}
