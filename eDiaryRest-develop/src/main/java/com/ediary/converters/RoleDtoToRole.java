package com.ediary.converters;

import com.ediary.DTO.RoleDto;
import com.ediary.domain.security.Role;
import com.ediary.repositories.security.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RoleDtoToRole implements Converter<RoleDto, Role> {

    private final RoleRepository roleRepository;

    @Nullable
    @Synchronized
    @Override
    public Role convert(RoleDto source) {

        if(source == null){
            return null;
        }

        if (source.getId() != null) {
            return roleRepository.findById(source.getId()).orElse(null);
        }

        return null;

    }
}
