package com.ediary.converters;

import com.ediary.DTO.ParentCouncilDto;
import com.ediary.domain.Parent;
import com.ediary.domain.ParentCouncil;
import com.ediary.domain.Student;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ParentCouncilToParentCouncilDto implements Converter<ParentCouncil, ParentCouncilDto> {

    private final ParentToParentDto parentToParentDto;

    @Nullable
    @Synchronized
    @Override
    public ParentCouncilDto convert(ParentCouncil source) {

        if (source == null) {
            return null;
        }

        ParentCouncilDto parentCouncilDto = new ParentCouncilDto();

        parentCouncilDto.setId(source.getId());

        //Class
        parentCouncilDto.setSchoolClassName(source.getSchoolClass().getName());
        parentCouncilDto.setSchoolClassId(source.getSchoolClass().getId());

        //Parents
        parentCouncilDto.setParents(source.getParents()
                .stream()
                .map(parentToParentDto::convert)
                .sorted(Comparator
                        .comparing(parentDto ->
                                Arrays.stream(parentDto
                                        .getUserName()
                                        .split(" ")).skip(1).findFirst().get()))
                .collect(Collectors.toList()));


        return parentCouncilDto;
    }
}
