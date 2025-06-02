package com.example.crabfood_api.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeHelper {

    public static LocalDateTime convertToLocalDateTime(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime payDate = LocalDateTime.parse(date, formatter);

        return payDate;
    }
}
