package ru.practicum.mainService;

import java.time.format.DateTimeFormatter;

public class GetFormatter {
    public static DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
}
