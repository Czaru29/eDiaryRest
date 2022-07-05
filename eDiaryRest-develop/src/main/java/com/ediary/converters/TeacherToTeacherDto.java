package com.ediary.converters;


import com.ediary.DTO.TeacherDto;
import com.ediary.domain.Teacher;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TeacherToTeacherDto implements Converter<Teacher, TeacherDto> {


    @Nullable
    @Synchronized
    @Override
    public TeacherDto convert(Teacher source) {

        if (source == null) {
            return null;
        }

        TeacherDto teacherDto = new TeacherDto();

        teacherDto.setUserId(source.getUser().getId());
        teacherDto.setId(source.getId());

        teacherDto.setName(source.getUser().getFirstName() + " " + source.getUser().getLastName());

        return teacherDto;
    }
}
