package com.ediary.converters;

import com.ediary.DTO.GradeDto;
import com.ediary.domain.Grade;
import com.ediary.domain.Student;
import com.ediary.domain.Subject;
import com.ediary.domain.Teacher;
import com.ediary.repositories.StudentRepository;
import com.ediary.repositories.SubjectRepository;
import com.ediary.repositories.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class GradeDtoToGrade implements Converter<GradeDto, Grade> {

    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;

    @Synchronized
    @Nullable
    @Override
    public Grade convert(GradeDto source) {

        if(source == null){
            return null;
        }

        final Grade grade = new Grade();
        grade.setId(source.getId());
        grade.setValue(source.getValue());
        grade.setWeight(source.getWeight());
        grade.setDescription(source.getDescription());
        grade.setDate(source.getDate());

        //Teacher
        Optional<Teacher> teacherOptional = teacherRepository.findById(source.getTeacherId());
        if(teacherOptional.isPresent()) {
            grade.setTeacher(teacherOptional.get());
        }

        //Subject
        if (source.getSubjectId() != null) {
            Optional<Subject> subjectOptional = subjectRepository.findById(source.getSubjectId());
            if (subjectOptional.isPresent()) {
                grade.setSubject(subjectOptional.get());
            }
        }

        //Student
        Optional<Student> studentOptional = studentRepository.findById(source.getStudentId());
        if(studentOptional.isPresent()) {
            grade.setStudent(studentOptional.get());
        }

        return grade;
    }
}
