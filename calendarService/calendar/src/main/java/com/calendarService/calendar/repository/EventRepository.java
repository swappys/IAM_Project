package com.calendarService.calendar.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.calendarService.calendar.models.EventModel;

public interface EventRepository extends JpaRepository<EventModel, Long>{
    
}
