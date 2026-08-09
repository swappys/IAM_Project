package com.calendarService.calendar.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.calendarService.calendar.models.EventModel;
import com.calendarService.calendar.repository.EventRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(EventRepository eventRepository){
        return args->{
            eventRepository.save(
                EventModel.builder()
                            .title("Team Meeting")
                            .time(LocalDateTime.of(2026, 8, 10, 10, 0))
                            .build()
            );
            eventRepository.save(
                EventModel.builder()
                            .title("Management Meeting")
                            .time(LocalDateTime.of(2026, 8, 11, 10, 0))
                            .build()
            );
            eventRepository.save(
                EventModel.builder()
                            .title("Scrum Meeting")
                            .time(LocalDateTime.of(2026, 8, 11, 12, 0))
                            .build()
            );
        };
    }
}
