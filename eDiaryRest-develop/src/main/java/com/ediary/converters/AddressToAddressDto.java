package com.ediary.converters;

import com.ediary.DTO.AddressDto;
import com.ediary.domain.Address;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class AddressToAddressDto implements Converter<Address, AddressDto> {

    @Synchronized
    @Nullable
    @Override
    public AddressDto convert(Address source) {

        if(source == null){
            return null;
        }

        final AddressDto addressDto = new AddressDto();

        addressDto.setId(source.getId());
        addressDto.setStreet(source.getStreet());
        addressDto.setCity(source.getCity());
        addressDto.setState(source.getState());
        addressDto.setZip(source.getZip());
        addressDto.setPhoneNumber(source.getPhoneNumber());

        //User
        //Condition for school address
        if (source.getUser() != null){
            addressDto.setUserId(source.getUser().getId());
        }

        return addressDto;
    }
}
