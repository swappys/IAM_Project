package com.calendarService.calendar.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.calendarService.calendar.models.ApiResponse;
import com.calendarService.calendar.models.EventModel;
import com.calendarService.calendar.models.RequestModel;
import com.calendarService.calendar.repository.EventRepository;

@Service
public class CalendarService {

    final EventRepository eventRepository;

    CalendarService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventModel> fetchEvents(){
        return eventRepository.findAll();
    }

    public ResponseEntity<ApiResponse> addEvent(RequestModel model){
        try{
            EventModel eventModel = EventModel.builder()
                                                .title(model.title)
                                                .time(model.time)
                                                .build();
            EventModel savedEvent = eventRepository.save(eventModel);
            ApiResponse response = ApiResponse.builder()
                                            .message("Event saved successfully")
                                            .error(false)
                                            .data(savedEvent)
                                            .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch(Exception ex){
            ApiResponse response = ApiResponse.builder()
                                            .message("Failed to save event")
                                            .error(true)
                                            .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
