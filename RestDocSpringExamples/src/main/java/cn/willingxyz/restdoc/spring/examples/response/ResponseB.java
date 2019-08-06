package cn.willingxyz.restdoc.spring.examples.response;

import lombok.Data;

import java.util.List;

/**
 * ResponseB类注释
 */
@Data
public class ResponseB {
    /**
     * 参数name
     */
    private String _name;
    /**
     * 参数ResponseC
     */
    private ResponseC _responseC;
    /**
     * 参数responseCArray
     */
    private ResponseC[] _responseCArray;
    /**
     * 参数responseCList
     */
    private List<ResponseC> _responseCList;
}
