package com.calendarService.calendar.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.calendarService.calendar.models.ApiResponse;
import com.calendarService.calendar.models.EventModel;
import com.calendarService.calendar.models.RequestModel;
import com.calendarService.calendar.repository.EventRepository;


@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CalendarService calendarService;

    private EventModel event;

    private RequestModel request;


    @BeforeEach
    void setUp() {

        event = EventModel.builder()
                .id(1L)
                .title("Team Meeting")
                .time(
                    LocalDateTime.of(2026,8,10,10,0)
                )
                .build();


        request = new RequestModel(
                "Team Meeting",
                LocalDateTime.of(2026,8,10,10,0)
        );
    }


    // --------------------------------------------------
    // fetchEvents()
    // --------------------------------------------------

    @Test
    void fetchEvents_shouldReturnEvents() {

        when(eventRepository.findAll()).thenReturn(List.of(event));   

        List<EventModel> result = calendarService.fetchEvents();

        assertNotNull(result);
        
        assertEquals(1,result.size());

        verify(eventRepository,times(1)).findAll();
    }


    @Test
    void fetchEvents_shouldReturnEmptyList() {

        when(eventRepository.findAll()).thenReturn(List.of());
 
        List<EventModel> result =calendarService.fetchEvents();
        assertNotNull(result);

        verify(eventRepository,times(1)).findAll();
    }


    // --------------------------------------------------
    // addEvent()
    // --------------------------------------------------

    @Test
    void addEvent_shouldSaveEventSuccessfully() {

        when(eventRepository.save(any(EventModel.class))).thenReturn(event);

        ResponseEntity<ApiResponse> response = calendarService.addEvent(request);

        assertEquals(HttpStatus.CREATED,response.getStatusCode() );

        assertNotNull(response.getBody());

    }


    @Test
    void addEvent_shouldCreateEventWithCorrectData() {

        when(eventRepository.save(any(EventModel.class))).thenReturn(event);

        calendarService.addEvent(request);

        verify(eventRepository).save(argThat(savedEvent -> savedEvent.getTitle().equals(
                    "Team Meeting"
                )
            )
        );
    }


    @Test
    void addEvent_shouldReturnInternalServerErrorWhenSaveFails() {


        when(eventRepository.save(any(EventModel.class)))
                .thenThrow(
                    new RuntimeException(
                        "Database error"
                    )
                );

        ResponseEntity<ApiResponse> response = calendarService.addEvent(request);

        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        assertNotNull(
                response.getBody()
        );

    }
}