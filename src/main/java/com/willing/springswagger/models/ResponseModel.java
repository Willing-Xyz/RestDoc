package com.willing.springswagger.models;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResponseModel {
    /**
     * 响应码
     */
    private HttpStatus _statusCode;


}
