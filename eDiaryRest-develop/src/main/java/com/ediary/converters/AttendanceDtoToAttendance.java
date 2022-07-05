package com.ediary.converters;

import com.ediary.DTO.AttendanceDto;
import com.ediary.domain.Attendance;
import com.ediary.domain.Student;
import com.ediary.repositories.ExtenuationRepository;
import com.ediary.repositories.LessonRepository;
import com.ediary.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AttendanceDtoToAttendance implements Converter<AttendanceDto, Attendance> {

    private final LessonDtoToLesson lessonDtoToLesson;
    private final LessonRepository lessonRepository;
    private final StudentRepository studentRepository;
    private final ExtenuationRepository extenuationRepository;

    @Synchronized
    @Nullable
    @Override
    public Attendance convert(AttendanceDto source) {

        if(source == null){
            return null;
        }

        final Attendance attendance = new Attendance();
        attendance.setId(source.getId());
        attendance.setStatus(source.getStatus());

        //Lesson
        attendance.setLesson(lessonRepository.findById(source.getLessonId()).orElse(null));

        //Student
        Optional<Student> studentOptional = studentRepository.findById(source.getStudentId());
        if (studentOptional.isPresent()) {
            attendance.setStudent(studentOptional.get());
        }


        //Extenuation
        attendance.setExtenuations(new HashSet<>(extenuationRepository.findAllByAttendancesId(source.getId())));

        return attendance;
    }
}
