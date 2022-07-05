package com.ediary.converters;

import com.ediary.DTO.AddressDto;
import com.ediary.domain.Address;
import com.ediary.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressDtoToAddress implements Converter<AddressDto, Address> {

    private final UserRepository userRepository;

    @Override
    public Address convert(AddressDto source) {
        if (source == null) {
            return null;
        }

        final Address address = new Address();

        address.setId(source.getId());
        address.setStreet(source.getStreet());
        address.setCity(source.getCity());
        address.setState(source.getState());
        address.setZip(source.getZip());
        address.setPhoneNumber(source.getPhoneNumber());

        //User
        if(source.getId() != null) {
            address.setUser(userRepository.findById(source.getId()).orElse(null));
        }

        return address;
    }
}
