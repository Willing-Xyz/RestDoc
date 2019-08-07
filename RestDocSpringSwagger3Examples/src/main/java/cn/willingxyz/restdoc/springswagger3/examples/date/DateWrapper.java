package cn.willingxyz.restdoc.springswagger3.examples.date;

import lombok.Data;

import java.time.*;
import java.util.Date;

@Data
public class DateWrapper {
    private Date _date;
    private LocalDateTime _localDateTime;
    private LocalDate _localDate;
    private LocalTime _localTime;
    private Year _year;
    private YearMonth _yearMonth;
    private MonthDay _monthDay;
    private Instant _instant;
}
