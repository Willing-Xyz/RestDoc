package com.willing.springswagger.models;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResponseModel {
    /**
     * 响应状态码
     */
    private HttpStatus _statusCode;
    private ReturnModel _returnModel = new ReturnModel();
}
