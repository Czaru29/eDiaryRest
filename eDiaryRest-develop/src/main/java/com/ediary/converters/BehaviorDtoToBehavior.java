package com.ediary.converters;

import com.ediary.DTO.BehaviorDto;
import com.ediary.domain.Behavior;
import com.ediary.domain.Student;
import com.ediary.domain.Teacher;
import com.ediary.repositories.StudentRepository;
import com.ediary.repositories.TeacherRepository;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class BehaviorDtoToBehavior implements Converter<BehaviorDto, Behavior> {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Synchronized
    @Nullable
    @Override
    public Behavior convert(BehaviorDto source) {

        if(source == null){
            return null;
        }

        final Behavior behavior = new Behavior();
        behavior.setId(source.getId());
        behavior.setDate(source.getDate());
        behavior.setContent(source.getContent());
        behavior.setPositive(source.isPositive());

        //Student
        Optional<Student> studentOptional = studentRepository.findById(source.getStudentId());
        if(studentOptional.isPresent()) {
            behavior.setStudent(studentOptional.get());
        }

        //Teacher
        Optional<Teacher> teacherOptional = teacherRepository.findById(source.getTeacherId());
        if(teacherOptional.isPresent()) {
            behavior.setTeacher(teacherOptional.get());
        }

        return behavior;
    }
}
