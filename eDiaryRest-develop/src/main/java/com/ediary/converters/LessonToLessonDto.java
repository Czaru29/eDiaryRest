package com.ediary.converters;

import com.ediary.DTO.LessonDto;
import com.ediary.domain.Lesson;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class LessonToLessonDto implements Converter<Lesson, LessonDto> {

    @Synchronized
    @Nullable
    @Override
    public LessonDto convert(Lesson source) {

        if(source == null){
            return null;
        }

        final LessonDto lessonDto = new LessonDto();
        lessonDto.setId(source.getId());
        lessonDto.setName(source.getName());
        lessonDto.setDate(source.getDate());

        //Class
        lessonDto.setClassId(source.getSchoolClass().getId());
        lessonDto.setClassName(source.getSchoolClass().getName());

        //Topic
        if (source.getTopic() != null) {
            lessonDto.setTopicId(source.getTopic().getId());
            lessonDto.setTopicName(source.getTopic().getName());
        }

        //Subject
        if (source.getSubject() != null) {
            lessonDto.setSubjectId(source.getSubject().getId());
            lessonDto.setSubjectName(source.getSubject().getName());
        }

        return lessonDto;
    }
}
