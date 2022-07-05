package com.ediary.converters;

import com.ediary.DTO.NoticeDto;
import com.ediary.domain.Notice;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class NoticeToNoticeDto implements Converter<Notice, NoticeDto> {

    @Synchronized
    @NonNull
    @Override
    public NoticeDto convert(Notice source) {

        final NoticeDto noticeDto = new NoticeDto();
        noticeDto.setId(source.getId());
        noticeDto.setTitle(source.getTitle());
        noticeDto.setContent(source.getContent());
        noticeDto.setDate(source.getDate());

        //author
        noticeDto.setAuthorId(source.getUser().getId());
        noticeDto.setAuthorName(source.getUser().getFirstName() + " " + source.getUser().getLastName());

        return noticeDto;
    }
}
