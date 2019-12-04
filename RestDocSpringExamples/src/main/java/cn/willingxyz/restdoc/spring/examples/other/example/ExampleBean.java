package cn.willingxyz.restdoc.spring.examples.other.example;

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
     * @example 😊他明白😠
     */
    private String _example;

    /**
     * @returnExample 🙂getName🙂
     */
    public String getName() {
        return null;
    }
    /**
     * @paramExample 😠setTitle😠
     */
    public void setTitle(String title)
    {
    }
}
