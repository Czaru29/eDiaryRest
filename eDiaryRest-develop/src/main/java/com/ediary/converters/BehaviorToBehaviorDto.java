package com.ediary.converters;

import com.ediary.DTO.BehaviorDto;
import com.ediary.domain.Behavior;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class BehaviorToBehaviorDto implements Converter<Behavior, BehaviorDto> {

    @Synchronized
    @Nullable
    @Override
    public BehaviorDto convert(Behavior source) {

        if(source == null){
            return null;
        }

        final BehaviorDto behaviorDto = new BehaviorDto();
        behaviorDto.setId(source.getId());
        behaviorDto.setDate(source.getDate());
        behaviorDto.setContent(source.getContent());
        behaviorDto.setPositive(source.isPositive());

        //Student
        if (source.getStudent() != null) {
            behaviorDto.setStudentId(source.getStudent().getId());
            behaviorDto.setStudentName(
                    source.getStudent().getUser().getFirstName() + " " + source.getStudent().getUser().getLastName()
            );
        }

        //Teacher
        behaviorDto.setTeacherId(source.getTeacher().getId());
        behaviorDto.setTeacherName(
                source.getTeacher().getUser().getFirstName() + " " + source.getTeacher().getUser().getLastName()
        );

        return behaviorDto;
    }
}
