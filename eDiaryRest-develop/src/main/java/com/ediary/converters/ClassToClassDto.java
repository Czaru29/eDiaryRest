package com.ediary.converters;

import com.ediary.DTO.SchoolClassDto;
import com.ediary.domain.*;
import com.ediary.domain.Class;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ClassToClassDto implements Converter<Class, SchoolClassDto> {

    private final StudentToStudentDto studentToStudentDto;

    @Nullable
    @Synchronized
    @Override
    public SchoolClassDto convert(Class source) {

        if (source == null) {
            return null;
        }

        final SchoolClassDto schoolClassDto = new SchoolClassDto();

        schoolClassDto.setId(source.getId());
        schoolClassDto.setName(source.getName());

        //Student Council
        if (source.getStudentCouncil() != null) {
            schoolClassDto.setStudentCouncilId(source.getStudentCouncil().getId());
        }

        //Teacher
        schoolClassDto.setTeacherName(source.getTeacher().getUser().getFirstName() + " " + source.getTeacher().getUser().getLastName());
        schoolClassDto.setTeacherId(source.getTeacher().getId());

        //Parent Council
        if (source.getParentCouncil() != null) {
            schoolClassDto.setParentCouncilId(source.getParentCouncil().getId());
        }

        if (source.getStudents() != null) {
            //Students sorted by last name
            schoolClassDto.setStudents(source.getStudents()
                    .stream()
                    .map(studentToStudentDto::convert)
                    .sorted(Comparator
                            .comparing(studentDto ->
                                    Arrays.stream(studentDto
                                            .getUserName()
                                            .split(" ")).skip(1).findFirst().get()))
                    .collect(Collectors.toList()));
        }

        //Events
        schoolClassDto.setEventsId(source.getEvents().stream()
                .map(Event::getId)
                .collect(Collectors.toList()));

        //School Periods
        schoolClassDto.setSchoolPeriodsId(source.getSchoolPeriods().stream()
                .map(SchoolPeriod::getId)
                .collect(Collectors.toList()));

        //Lessons
        schoolClassDto.setLessonsId(source.getLessons().stream()
                .map(Lesson::getId)
                .collect(Collectors.toList()));


        return schoolClassDto;
    }
}
