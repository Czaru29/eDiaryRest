package com.ediary.converters;

import com.ediary.DTO.StudentDto;
import com.ediary.domain.Student;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class StudentToStudentDto implements Converter<Student, StudentDto> {

    @Synchronized
    @Nullable
    @Override
    public StudentDto convert(Student source) {

        if(source == null){
            return null;
        }

        StudentDto studentDto = new StudentDto();

        studentDto.setUserId(source.getUser().getId());
        studentDto.setId(source.getId());
        studentDto.setUserName(source.getUser().getFirstName() + " " + source.getUser().getLastName());

        if (source.getSchoolClass() != null) {
            studentDto.setClassName(source.getSchoolClass().getName());
        }

        return studentDto;
    }

    @Synchronized
    @Nullable
    public StudentDto convertForParent(Student source) {

        if(source == null){
            return null;
        }

        final StudentDto studentDto = new StudentDto();
        studentDto.setId(source.getId());
        studentDto.setUserName(source.getUser().getFirstName() + " " + source.getUser().getLastName());


        if (source.getSchoolClass() != null) {
            studentDto.setClassName(source.getSchoolClass().getName());
        }


        return studentDto;

    }
}
