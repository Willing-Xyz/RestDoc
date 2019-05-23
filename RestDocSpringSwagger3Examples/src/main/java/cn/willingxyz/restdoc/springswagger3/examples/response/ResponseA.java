package cn.willingxyz.restdoc.springswagger3.examples.response;

import lombok.Data;

import java.util.List;

@Data
public class ResponseA {
    private String _name;
    private ResponseB _responseB;
    private List<ResponseB> _responseBList;
    private ResponseB[] _responseBArray;
}
