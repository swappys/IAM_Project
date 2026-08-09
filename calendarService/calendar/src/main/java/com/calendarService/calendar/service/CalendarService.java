package com.calendarService.calendar.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class CalendarService {
    
    public List<Map<String,String>> fetchEvents(){
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
