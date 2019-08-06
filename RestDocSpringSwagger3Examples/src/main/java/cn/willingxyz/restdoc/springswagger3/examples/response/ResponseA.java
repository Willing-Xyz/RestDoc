package cn.willingxyz.restdoc.springswagger3.examples.response;

import lombok.Data;

import java.util.List;

/**
 * 参数ResponseA类注释
 */
@Data
public class ResponseA {
    /**
     * 参数name
     */
    private String _name;
    /**
     * 参数responseB
     */
    private ResponseB _responseB;
    /**
     * 参数responseBList
     */
    private List<ResponseB> _responseBList;
    /**
     * 参数responseBArray
     */
    private ResponseB[] _responseBArray;
}
