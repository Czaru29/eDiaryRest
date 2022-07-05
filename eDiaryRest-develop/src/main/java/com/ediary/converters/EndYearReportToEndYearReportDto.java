package com.ediary.converters;

import com.ediary.DTO.EndYearReportDto;
import com.ediary.DTO.EndYearReportDtoShort;
import com.ediary.domain.EndYearReport;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EndYearReportToEndYearReportDto implements Converter<EndYearReport, EndYearReportDto> {

    @Nullable
    @Synchronized
    @Override
    public EndYearReportDto convert(EndYearReport source) {

        final EndYearReportDto dto = new EndYearReportDto();

        dto.setId(source.getId());
        dto.setYear(source.getYear());

        //Teacher
        if (source.getUserType() == EndYearReport.Type.TEACHER && source.getTeacher() != null) {
            dto.setTeacherId(source.getTeacher().getId());
            dto.setTeacherName(source.getTeacher().getUser().getFirstName() + " " + source.getTeacher().getUser().getLastName());
        }

        if (source.getUserType() == EndYearReport.Type.STUDENT && source.getStudent() != null) {
            dto.setStudentId(source.getStudent().getId());
            dto.setStudentName(source.getStudent().getUser().getFirstName() + " " + source.getStudent().getUser().getLastName());
        }

        return dto;
    }

    public EndYearReportDtoShort convertShort(EndYearReport source) {

        final EndYearReportDtoShort dto = new EndYearReportDtoShort();

        dto.setId(source.getId());
        dto.setYear(source.getYear());

        if (source.getUserType() == EndYearReport.Type.TEACHER && source.getTeacher() != null) {
            dto.setUserId(source.getTeacher().getId());
            dto.setName(source.getTeacher().getUser().getFirstName() + " " + source.getTeacher().getUser().getLastName());
        }

        if (source.getUserType() == EndYearReport.Type.STUDENT && source.getStudent() != null) {
            dto.setUserId(source.getStudent().getId());
            dto.setName(source.getStudent().getUser().getFirstName() + " " + source.getStudent().getUser().getLastName());
        }

        return dto;
    }
}
