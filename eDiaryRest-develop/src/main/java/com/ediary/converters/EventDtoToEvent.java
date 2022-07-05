package com.ediary.converters;

import com.ediary.DTO.EventDto;
import com.ediary.domain.Class;
import com.ediary.domain.Event;
import com.ediary.domain.Teacher;
import com.ediary.repositories.ClassRepository;
import com.ediary.repositories.TeacherRepository;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class EventDtoToEvent implements Converter<EventDto, Event> {

    private final ClassRepository classRepository;
    private final TeacherRepository teacherRepository;

    @Synchronized
    @Nullable
    @Override
    public Event convert(EventDto source) {

        if(source == null){
            return null;
        }

        final Event event = new Event();
        event.setId(source.getId());
        event.setDescription(source.getDescription());
        event.setCreateDate(source.getCreateDate());
        event.setDate(source.getDate());
        event.setType(source.getType());

        //Class
        Optional<Class> classOptional = classRepository.findById(source.getClassId());
        if (classOptional.isPresent()) {
            event.setSchoolClass(classOptional.get());
        }

        //Teacher
        Optional<Teacher> teacherOptional = teacherRepository.findById(source.getTeacherId());
        if(teacherOptional.isPresent()) {
            event.setTeacher(teacherOptional.get());
        }

        return event;
    }
}
