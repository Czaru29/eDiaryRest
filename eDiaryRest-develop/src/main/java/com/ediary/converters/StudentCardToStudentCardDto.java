package com.ediary.converters;

import com.ediary.DTO.StudentCardDto;
import com.ediary.domain.StudentCard;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StudentCardToStudentCardDto implements Converter<StudentCard, StudentCardDto> {

    @Override
    public StudentCardDto convert(StudentCard source) {

        if (source == null) {
            return null;
        }

        StudentCardDto studentCardDto = new StudentCardDto();

        studentCardDto.setId(source.getId());
        studentCardDto.setDate(source.getDate());
        studentCardDto.setContent(source.getContent());

        //Student
        studentCardDto.setStudentId(source.getStudent().getId());
        studentCardDto.setStudentName(source.getStudent().getUser().getFirstName() + " " + source.getStudent().getUser().getLastName());

        //Teacher
        studentCardDto.setTeacherId(source.getTeacher().getId());
        studentCardDto.setTeacherName(source.getTeacher().getUser().getFirstName() + " " + source.getTeacher().getUser().getLastName());

        return studentCardDto;
    }
}
