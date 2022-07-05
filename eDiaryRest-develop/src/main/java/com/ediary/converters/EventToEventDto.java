package com.ediary.converters;

import com.ediary.DTO.EventDto;
import com.ediary.domain.Event;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class EventToEventDto implements Converter<Event, EventDto> {

    @Synchronized
    @Nullable
    @Override
    public EventDto convert(Event source) {

        if(source == null){
            return null;
        }

        final EventDto eventDto = new EventDto();
        eventDto.setId(source.getId());
        eventDto.setDescription(source.getDescription());
        eventDto.setCreateDate(source.getCreateDate());
        eventDto.setDate(source.getDate());
        eventDto.setType(source.getType());

        //Class
        if (source.getSchoolClass() != null) {
            eventDto.setClassId(source.getSchoolClass().getId());
            eventDto.setClassName(source.getSchoolClass().getName());
        }

        //Teacher
        eventDto.setTeacherId(source.getTeacher().getId());
        eventDto.setTeacherName(
                source.getTeacher().getUser().getFirstName() + " " + source.getTeacher().getUser().getLastName()
        );

        return eventDto;
    }
}
