package cn.willingxyz.restdoc.springswagger3.examples.date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 日期类型
 */
@RestController
@RequestMapping("/date")
public class DateController {

    /**
     * 日期在参数中
     */
    @GetMapping("/parameter")
    public void parameter(@DateTimeFormat(pattern = "yyyy-MM-dd") Date date)
    {
        System.out.println(date);
    }

    /**
     * 日期在响应中
     */
    @GetMapping("/response")
    public Date response()
    {
        return null;
    }



}
