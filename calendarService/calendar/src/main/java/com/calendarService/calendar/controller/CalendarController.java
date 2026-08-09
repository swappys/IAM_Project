package com.calendarService.calendar.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/calendar")
public class CalendarController {
    

    @GetMapping()
    public List<Map<String, String>> getCalendar(){
        return List.of(
            Map.of(
                "Title","Team Meeting",
                "Time","10:00 Monday"
            ),
            Map.of(
                "Title","Project Demo",
                "Time","15:00 Wednesday"
            ),
            Map.of(
                "Title","Project Meeting",
                "Time","12:00 Wednesday"
            ),
            Map.of(
                "Title","Management Meeting",
                "Time","13:00 Friday"
            )
        );
    }
}
