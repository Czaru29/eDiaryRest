package com.ediary.converters;

import com.ediary.DTO.ExtenuationDto;
import com.ediary.domain.Attendance;
import com.ediary.domain.Extenuation;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ExtenuationToExtenuationDto implements Converter<Extenuation, ExtenuationDto> {

    private final AttendanceToAttendanceDto attendanceToAttendanceDto;

    @Nullable
    @Synchronized
    @Override
    public ExtenuationDto convert(Extenuation source) {

        if(source == null){
            return null;
        }

        final ExtenuationDto extenuationDto = new ExtenuationDto();

        extenuationDto.setId(source.getId());
        extenuationDto.setDescription(source.getDescription());
        extenuationDto.setStatus(source.getStatus());

        //Parent
        extenuationDto.setParentName(source.getParent().getUser().getFirstName() + " " + source.getParent().getUser().getLastName());
        extenuationDto.setParentId(source.getParent().getId());

        //Attendances
        extenuationDto.setAttendancesId(source.getAttendances().stream()
                .map(Attendance::getId)
                .collect(Collectors.toList()));

        extenuationDto.setAttendances(source.getAttendances().stream()
                .map(attendanceToAttendanceDto::convert)
                .collect(Collectors.toList()));

        return extenuationDto;
    }
}
