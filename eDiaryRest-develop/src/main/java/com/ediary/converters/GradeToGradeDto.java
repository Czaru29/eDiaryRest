package com.ediary.converters;

import com.ediary.DTO.GradeDto;
import com.ediary.domain.Grade;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class GradeToGradeDto implements Converter<Grade, GradeDto> {

    @Synchronized
    @Nullable
    @Override
    public GradeDto convert(Grade source) {

        if(source == null){
            return null;
        }

        final GradeDto gradeDto = new GradeDto();
        gradeDto.setId(source.getId());
        gradeDto.setValue(source.getValue());
        gradeDto.setWeight(source.getWeight());
        gradeDto.setDescription(source.getDescription());
        gradeDto.setDate(source.getDate());
        
        //Teacher
        gradeDto.setTeacherId(source.getTeacher().getId());
        gradeDto.setTeacherName(
                source.getTeacher().getUser().getFirstName() + " " + source.getTeacher().getUser().getLastName()
        );

        //Subject
        if (source.getSubject() != null) {
            gradeDto.setSubjectId(source.getSubject().getId());
            gradeDto.setSubjectName(source.getSubject().getName());

        }

        //Student
        if (source.getStudent() != null) {
            gradeDto.setStudentId(source.getStudent().getId());
            gradeDto.setStudentName(
                    source.getStudent().getUser().getFirstName() + " " + source.getStudent().getUser().getLastName()
            );
        }

        return gradeDto;
    }
}
