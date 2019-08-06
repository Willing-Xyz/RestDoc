package cn.willingxyz.restdoc.spring.examples.date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

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
     * 日期在body参数里
     */
    @PostMapping("/parameter/body")
    public void parameterInBody(@RequestBody DateWrapper parameterBody)
    {
    }

    /**
     * 日期在响应中
     */
    @GetMapping("/response")
    public Date response()
    {
        return null;
    }

    /**
     * 日期在响应的类中
     */
    @GetMapping("/response/complex")
    public DateWrapper responseComplex()
    {
        return null;
    }



}
