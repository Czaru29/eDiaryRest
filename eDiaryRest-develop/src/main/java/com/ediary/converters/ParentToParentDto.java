package com.ediary.converters;

import com.ediary.DTO.ParentDto;
import com.ediary.domain.Parent;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class ParentToParentDto implements Converter<Parent, ParentDto> {

    @Nullable
    @Synchronized
    @Override
    public ParentDto convert(Parent source) {

        if (source == null) {
            return null;
        }

        final ParentDto parentDto = new ParentDto();
        parentDto.setId(source.getId());
        parentDto.setUserId(source.getUser().getId());
        parentDto.setUserName(source.getUser().getFirstName() + " " + source.getUser().getLastName());

        return parentDto;
    }
}
