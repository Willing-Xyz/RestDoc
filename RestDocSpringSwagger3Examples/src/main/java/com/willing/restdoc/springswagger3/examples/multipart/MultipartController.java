package com.willing.restdoc.springswagger3.examples.multipart;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Part;

/**
 * multipart 文件上传
 */
@RestController
@RequestMapping("/multipart")
public class MultipartController {
    /**
     * 使用MultipartFile作为参数类型
     */
    @PostMapping("/upload/multipart")
    public void uploadMultipart(@RequestParam("model")MultipartFile model)
    {
    }

    /**
     * 不使用 @RequestParam
     */
    @PostMapping("/upload/multipart/withoutRequestParam")
    public void uploadMultipartWithoutRequestParam(MultipartFile model)
    {
    }

    /**
     * 多个参数
     */
    @PostMapping("/upload/multipart/multiparam")
    public void uploadMultipartMultiParam(MultipartFile file, String name)
    {
    }

    /**
     * 使用Part作为参数类型
     */
    @PostMapping("/upload/part")
    public void uploadPart(@RequestParam("file") Part model)
    {
    }
}
