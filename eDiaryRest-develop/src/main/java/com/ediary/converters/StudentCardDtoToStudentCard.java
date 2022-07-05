package com.ediary.converters;

import com.ediary.DTO.StudentCardDto;
import com.ediary.domain.StudentCard;
import com.ediary.repositories.StudentRepository;
import com.ediary.repositories.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StudentCardDtoToStudentCard implements Converter<StudentCardDto, StudentCard> {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Override
    public StudentCard convert(StudentCardDto source) {

        if (source == null) {
            return null;
        }

        StudentCard studentCard = new StudentCard();

        studentCard.setId(source.getId());
        studentCard.setDate(source.getDate());
        studentCard.setContent(source.getContent());

        //Student
        studentCard.setStudent(studentRepository.findById(source.getStudentId()).orElse(null));

        //Teacher
        studentCard.setTeacher(teacherRepository.findById(source.getTeacherId()).orElse(null));

        return studentCard;
    }
}
