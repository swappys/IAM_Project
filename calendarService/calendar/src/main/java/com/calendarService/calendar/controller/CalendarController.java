package com.calendarService.calendar.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.calendarService.calendar.models.ApiResponse;
import com.calendarService.calendar.models.EventModel;
import com.calendarService.calendar.models.RequestModel;
import com.calendarService.calendar.service.CalendarService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

    final CalendarService calendarService;

    CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    //Get all events.
    @GetMapping("/getEvents")
    public List<EventModel> getCalendar() {
        return calendarService.fetchEvents();

    }

    //Add new events.
    @PostMapping("/addEvent")
    public ResponseEntity<ApiResponse> addEvent(@Valid @RequestBody RequestModel entity) {
        return calendarService.addEvent(entity);

    }

}
