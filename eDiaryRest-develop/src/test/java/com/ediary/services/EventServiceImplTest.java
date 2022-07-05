package com.ediary.services;

import com.ediary.domain.Event;
import com.ediary.repositories.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class EventServiceImplTest {

    @Mock
    EventRepository eventRepository;

    EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        eventService = new EventServiceImpl(eventRepository);
    }

    @Test
    void listEventsBySchoolClass() {
        when(eventRepository.findAllBySchoolClass(any())).thenReturn(Arrays.asList(
                Event.builder().id(1L).build(),
                Event.builder().id(2L).build()
        ));

        List<Event> events = eventService.listEventsBySchoolClass(any());

        assertEquals(1L, events.get(0).getId());
        assertEquals(2, events.size());
        verify(eventRepository, times(1)).findAllBySchoolClass(any());
    }

    @Test
    void listEventsByTeacher() {
        when(eventRepository.findAllByTeacher(any())).thenReturn(Arrays.asList(
                Event.builder().id(1L).build(),
                Event.builder().id(2L).build()
        ));

        List<Event> events = eventService.listEventsByTeacher(any());

        assertEquals(1L, events.get(0).getId());
        assertEquals(2, events.size());
        verify(eventRepository, times(1)).findAllByTeacher(any());
    }
}