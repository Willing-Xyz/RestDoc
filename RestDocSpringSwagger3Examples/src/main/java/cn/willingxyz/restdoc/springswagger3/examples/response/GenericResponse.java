package cn.willingxyz.restdoc.springswagger3.examples.response;

import lombok.Data;

import java.util.List;

@Data
public class GenericResponse<T> {
    private String _name;
    private T _data;

    @Data
    public static class Page<Item>
    {
        private List<Item> _list;
        private int _count;
    }
}
