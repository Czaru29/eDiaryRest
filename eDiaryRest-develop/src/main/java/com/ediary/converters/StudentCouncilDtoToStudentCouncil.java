package com.ediary.converters;


import com.ediary.DTO.StudentCouncilDto;
import com.ediary.DTO.StudentDto;
import com.ediary.domain.StudentCouncil;
import com.ediary.repositories.ClassRepository;
import com.ediary.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class StudentCouncilDtoToStudentCouncil implements Converter<StudentCouncilDto, StudentCouncil> {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;

    @Nullable
    @Synchronized
    @Override
    public StudentCouncil convert(StudentCouncilDto source) {

        if (source == null) {
            return null;
        }

        StudentCouncil studentCouncil = new StudentCouncil();

        studentCouncil.setId(source.getId());

        //Students
        if (source.getStudents() != null) {
            studentCouncil.setStudents(new HashSet<>(studentRepository.findAllById(source.getStudents()
                    .stream()
                    .map(StudentDto::getId)
                    .collect(Collectors.toList()))));

        }

        //Class
        studentCouncil.setSchoolClass(classRepository.findById(source.getSchoolClassId()).orElse(null));

        return studentCouncil;
    }
}
