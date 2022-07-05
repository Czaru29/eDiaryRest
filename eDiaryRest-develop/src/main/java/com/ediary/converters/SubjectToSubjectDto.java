package com.ediary.converters;

import com.ediary.DTO.SubjectDto;
import com.ediary.domain.Lesson;
import com.ediary.domain.Subject;
import com.ediary.domain.Teacher;
import com.ediary.domain.Topic;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SubjectToSubjectDto implements Converter<Subject, SubjectDto> {


    @Synchronized
    @Nullable
    @Override
    public SubjectDto convert(Subject source) {

        if (source == null) {
            return null;
        }

        final SubjectDto subjectDto = new SubjectDto();

        subjectDto.setId(source.getId());
        subjectDto.setName(source.getName());

        //Teachers
            subjectDto.setTeacherName(source.getTeacher().getUser().getFirstName()
                    + " " + source.getTeacher().getUser().getLastName());

            subjectDto.setTeacherId(source.getTeacher().getId());

        //Class
        if (source.getSchoolClass() != null) {
            subjectDto.setClassName(source.getSchoolClass().getName());
            subjectDto.setClassId(source.getSchoolClass().getId());
        }

        //Lessons
        if(source.getLessons() != null){
            subjectDto.setLessonsName(source.getLessons().stream()
                    .map(Lesson::getName)
                    .collect(Collectors.toList()));

            subjectDto.setLessonsId(source.getLessons().stream()
                    .map(Lesson::getId)
                    .collect(Collectors.toList()));
        }


        //Topic
        if(source.getTopics() != null) {
            subjectDto.setTopicsName(source.getTopics().stream()
                    .map(Topic::getName)
                    .collect(Collectors.toList()));

            subjectDto.setTopicsId(source.getTopics().stream()
                    .map(Topic::getId)
                    .collect(Collectors.toList()));
        }

        return subjectDto;
    }
}
