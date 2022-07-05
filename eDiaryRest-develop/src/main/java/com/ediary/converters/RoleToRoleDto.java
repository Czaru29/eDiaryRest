package com.ediary.converters;

import com.ediary.DTO.RoleDto;
import com.ediary.domain.security.Role;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class RoleToRoleDto implements Converter<Role, RoleDto> {

    @Override
    @Synchronized
    @Nullable
    public RoleDto convert(Role source) {
        if (source == null) {
            return null;
        }

        final RoleDto roleDto = new RoleDto();

        roleDto.setId(source.getId());
        roleDto.setName(source.getName());

        return roleDto;
    }
}
