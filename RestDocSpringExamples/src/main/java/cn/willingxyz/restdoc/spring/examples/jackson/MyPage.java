package cn.willingxyz.restdoc.spring.examples.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"current", "size", "orders", "hitCount", "searchCount", "pages"})
public class MyPage<T> extends Page<T> {
}
