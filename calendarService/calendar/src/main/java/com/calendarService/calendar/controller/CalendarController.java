package com.calendarService.calendar.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.calendarService.calendar.service.CalendarService;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/calendar")
public class CalendarController {
    
    final CalendarService calendarService;

    CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping()
    public List<Map<String, String>> getCalendar(){
            return calendarService.fetchEvents();
    
    }
}
