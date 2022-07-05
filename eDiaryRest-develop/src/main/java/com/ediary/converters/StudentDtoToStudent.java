package com.ediary.converters;

import com.ediary.DTO.StudentDto;
import com.ediary.domain.Student;
import com.ediary.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StudentDtoToStudent implements Converter<StudentDto, Student> {

    private final StudentRepository studentRepository;

    @Override
    public Student convert(StudentDto source) {
        if (source == null) {
            return null;
        }

        //Not sure if works correctly all the time, but seems good
        return studentRepository.findById(source.getId()).orElse(null);
    }
}
