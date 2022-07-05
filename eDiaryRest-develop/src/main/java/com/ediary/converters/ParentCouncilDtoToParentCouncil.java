package com.ediary.converters;

import com.ediary.DTO.ParentCouncilDto;
import com.ediary.DTO.ParentDto;
import com.ediary.domain.Parent;
import com.ediary.domain.ParentCouncil;
import com.ediary.repositories.ClassRepository;
import com.ediary.repositories.ParentRepository;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ParentCouncilDtoToParentCouncil implements Converter<ParentCouncilDto, ParentCouncil> {

    private final ClassRepository classRepository;
    private final ParentRepository parentRepository;

    @Nullable
    @Synchronized
    @Override
    public ParentCouncil convert(ParentCouncilDto source) {

        if (source == null) {
            return null;
        }

        ParentCouncil parentCouncil = new ParentCouncil();

        parentCouncil.setId(source.getId());

        //Class
        parentCouncil.setSchoolClass(classRepository.findById(source.getId()).orElse(null));

        //Parent
        parentCouncil.setParents(new HashSet<>(parentRepository.findAllById(source.getParents()
                .stream()
                .map(ParentDto::getId)
                .collect(Collectors.toList()))));

        return parentCouncil;
    }
}
