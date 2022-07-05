package com.ediary.services;

import com.ediary.domain.Class;
import com.ediary.domain.Event;
import com.ediary.domain.Teacher;

import java.util.List;

public interface EventService {

    List<Event> listEventsBySchoolClass(Class schoolClass);
    List<Event> listEventsByTeacher(Teacher teacher);
}
