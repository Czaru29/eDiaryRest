package com.ediary.converters;


import com.ediary.DTO.StudentCouncilDto;
import com.ediary.domain.Student;
import com.ediary.domain.StudentCouncil;
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
public class StudentCouncilToStudentCouncilDto implements Converter<StudentCouncil, StudentCouncilDto> {

    private final StudentToStudentDto studentToStudentDto;

    @Nullable
    @Synchronized
    @Override
    public StudentCouncilDto convert(StudentCouncil source) {

        if (source == null) {
            return null;
        }

        StudentCouncilDto studentCouncilDto = new StudentCouncilDto();

        studentCouncilDto.setId(source.getId());

        //Students
        studentCouncilDto.setStudents(source.getStudents()
                .stream()
                .map(studentToStudentDto::convert)
                .sorted(Comparator
                        .comparing(studentDto ->
                                Arrays.stream(studentDto
                                        .getUserName()
                                        .split(" ")).skip(1).findFirst().get()))
                .collect(Collectors.toList()));


        //Class
        studentCouncilDto.setSchoolClassName(source.getSchoolClass().getName());
        studentCouncilDto.setSchoolClassId(source.getSchoolClass().getId());

        return studentCouncilDto;
    }
}
