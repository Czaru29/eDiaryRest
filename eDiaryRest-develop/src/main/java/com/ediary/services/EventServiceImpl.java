package com.ediary.services;

import com.ediary.domain.Class;
import com.ediary.domain.Event;
import com.ediary.domain.Teacher;
import com.ediary.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public List<Event> listEventsBySchoolClass(Class schoolClass) {

        return eventRepository.findAllBySchoolClass(schoolClass);
    }

    @Override
    public List<Event> listEventsByTeacher(Teacher teacher) {

        return eventRepository.findAllByTeacher(teacher);
    }
}
