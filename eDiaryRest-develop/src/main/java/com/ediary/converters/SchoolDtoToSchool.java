package com.ediary.converters;

import com.ediary.DTO.SchoolDto;
import com.ediary.domain.School;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchoolDtoToSchool implements Converter<SchoolDto, School> {

    private final AddressDtoToAddress addressDtoToAddress;

    @Override
    public School convert(SchoolDto source) {

        if (source == null) {
            return null;
        }

        final School school = new School();

        school.setId(source.getId());
        school.setName(source.getName());
        school.setHeadmasterName(source.getHeadmasterName());
        school.setHeadmasterLastName(source.getHeadmasterLastName());
        school.setEmail(source.getEmail());
        school.setSchoolOffice(source.getSchoolOffice());

        //Address
        school.setAddress(addressDtoToAddress.convert(source.getAddressDto()));

        return school;
    }
}
