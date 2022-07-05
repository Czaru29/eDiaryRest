package com.ediary.converters;


import com.ediary.DTO.ExtenuationDto;
import com.ediary.domain.Extenuation;
import com.ediary.repositories.AttendanceRepository;
import com.ediary.repositories.ParentRepository;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@RequiredArgsConstructor
@Component
public class ExtenuationDtoToExtenuation implements Converter<ExtenuationDto, Extenuation> {

    private final ParentRepository parentRepository;
    private final AttendanceRepository attendanceRepository;

    @Nullable
    @Synchronized
    @Override
    public Extenuation convert(ExtenuationDto source) {

        if(source == null){
            return null;
        }

        final Extenuation extenuation = new Extenuation();

        extenuation.setId(source.getId());
        extenuation.setDescription(source.getDescription());
        extenuation.setStatus(source.getStatus());

        //Parent
        extenuation.setParent(parentRepository.findById(source.getParentId()).orElse(null));

        //Attendances
        if (source.getAttendancesId() != null) {
            extenuation.setAttendances(new HashSet<>(attendanceRepository.findAllById(source.getAttendancesId())));
        }

        return extenuation;
    }
}
