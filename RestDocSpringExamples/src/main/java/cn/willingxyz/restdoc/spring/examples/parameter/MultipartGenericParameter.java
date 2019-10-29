package cn.willingxyz.restdoc.spring.examples.parameter;

import lombok.Data;

/**
 * @author cweijan
 * @version 2019/10/29 11:23
 */
@Data
public class MultipartGenericParameter<T,E> {
    private T _t;
    private E _e;
}
