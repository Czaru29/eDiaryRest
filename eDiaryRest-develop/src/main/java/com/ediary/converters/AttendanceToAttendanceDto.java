package com.ediary.converters;

import com.ediary.DTO.AttendanceDto;
import com.ediary.domain.Attendance;
import com.ediary.domain.Extenuation;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class AttendanceToAttendanceDto implements Converter<Attendance, AttendanceDto> {

    private final LessonToLessonDto lessonToLessonDto;

    @Synchronized
    @Nullable
    @Override
    public AttendanceDto convert(Attendance source) {

        if(source == null){
            return null;
        }

        final AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setId(source.getId());
        attendanceDto.setStatus(source.getStatus());

        //Lesson
        attendanceDto.setLessonDto(lessonToLessonDto.convert(source.getLesson()));
        attendanceDto.setLessonId(source.getLesson().getId());

        //Student
        if (source.getStudent() != null) {
            attendanceDto.setStudentId(source.getStudent().getId());
            attendanceDto.setStudentName(
                    source.getStudent().getUser().getFirstName() + " " + source.getStudent().getUser().getLastName()
            );
            attendanceDto.setStudentClass(source.getStudent().getClass().getName());

        }

        //Extenuation
        if (source.getExtenuations() != null) {
            attendanceDto.setIsExcuseSent(!source.getExtenuations().isEmpty());
            attendanceDto.setExtenuationStatus(source.getExtenuations().stream()
                    .map(Extenuation::getStatus)
                    .collect(Collectors.toList()));
        }

        return attendanceDto;
    }
}
