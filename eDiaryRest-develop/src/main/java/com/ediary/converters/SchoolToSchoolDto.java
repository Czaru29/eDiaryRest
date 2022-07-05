package com.ediary.converters;

import com.ediary.DTO.SchoolDto;
import com.ediary.domain.School;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SchoolToSchoolDto implements Converter<School, SchoolDto> {

    private final AddressToAddressDto addressToAddressDto;

    @Override
    public SchoolDto convert(School source) {

        if (source == null) {
            return null;
        }

        final SchoolDto schoolDto = new SchoolDto();

        schoolDto.setId(source.getId());
        schoolDto.setName(source.getName());
        schoolDto.setHeadmasterName(source.getHeadmasterName());
        schoolDto.setHeadmasterLastName(source.getHeadmasterLastName());
        schoolDto.setEmail(source.getEmail());
        schoolDto.setSchoolOffice(source.getSchoolOffice());

        //Address
        schoolDto.setAddressDto(addressToAddressDto.convert(source.getAddress()));

        return schoolDto;
    }
}
