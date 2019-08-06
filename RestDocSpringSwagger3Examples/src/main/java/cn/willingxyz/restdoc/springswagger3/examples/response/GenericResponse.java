package cn.willingxyz.restdoc.springswagger3.examples.response;

import lombok.Data;

import java.util.List;

/**
 * GenericResponse类注释
 */
@Data
public class GenericResponse<T> {
    /**
     * 参数name
     */
    private String _name;
    /**
     * 参数date
     */
    private T _data;

    @Data
    public static class Page<Item>
    {
        /**
         * 参数list
         */
        private List<Item> _list;
        /**
         * 参数count
         */
        private int _count;
    }
}
