package com.ediary.converters;


import com.ediary.DTO.SchoolPeriodDto;
import com.ediary.domain.SchoolPeriod;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SchoolPeriodToSchoolPeriodDto implements Converter<SchoolPeriod, SchoolPeriodDto> {

    @Override
    public SchoolPeriodDto convert(SchoolPeriod source) {

        if (source == null) {
            return null;
        }

        SchoolPeriodDto schoolPeriodDto = new SchoolPeriodDto();

        schoolPeriodDto.setId(source.getId());
        schoolPeriodDto.setDescription(source.getDescripton());

        //Day
        schoolPeriodDto.setDayName(source.getDay().getName());
        schoolPeriodDto.setDayId(source.getDay().getId());

        //Duration
        schoolPeriodDto.setDurationNumber(source.getDuration().getNumber());
        schoolPeriodDto.setDurationId(source.getDuration().getId());
        schoolPeriodDto.setStartTime(source.getDuration().getStartTime().toString());
        schoolPeriodDto.setEndTime(source.getDuration().getEndTime().toString());

        //Subject
        schoolPeriodDto.setSubjectName(source.getSubject().getName());
        schoolPeriodDto.setSubjectId(source.getSubject().getId());

        //Teacher
        schoolPeriodDto.setTeacherName(source.getTeacher().getUser().getFirstName() + " " + source.getTeacher().getUser().getLastName());
        schoolPeriodDto.setTeacherId(source.getTeacher().getId());

        //Classroom
        schoolPeriodDto.setClassroomName(source.getClassroom().getName());
        schoolPeriodDto.setClassroomId(source.getClassroom().getId());

        //Class
        schoolPeriodDto.setSchoolClassName(source.getSchoolClass().getName());
        schoolPeriodDto.setSchoolClassId(source.getSchoolClass().getId());

        return schoolPeriodDto;
    }
}
