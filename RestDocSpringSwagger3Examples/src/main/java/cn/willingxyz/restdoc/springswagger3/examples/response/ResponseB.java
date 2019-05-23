package cn.willingxyz.restdoc.springswagger3.examples.response;

import lombok.Data;

import java.util.List;

@Data
public class ResponseB {
    private String _name;
    private ResponseC _responseC;
    private ResponseC[] _responseCArray;
    private List<ResponseC> _responseCList;
}
