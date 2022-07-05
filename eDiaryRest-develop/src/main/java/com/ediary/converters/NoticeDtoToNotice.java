package com.ediary.converters;

import com.ediary.DTO.NoticeDto;
import com.ediary.domain.Notice;
import com.ediary.domain.security.User;
import com.ediary.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class NoticeDtoToNotice implements Converter<NoticeDto, Notice> {

    private final UserRepository userRepository;

    @Synchronized
    @NonNull
    @Override
    public Notice convert(NoticeDto source) {

        final Notice notice = new Notice();
        notice.setId(source.getId());
        notice.setTitle(source.getTitle());
        notice.setContent(source.getContent());
        notice.setDate(source.getDate());

        //author
        if (Objects.nonNull(source.getAuthorId())) {
            Optional<User> userOptional = userRepository.findById(source.getAuthorId());
            userOptional.ifPresent(notice::setUser);
        }

        return notice;
    }
}
