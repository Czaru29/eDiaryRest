package com.ediary.converters;

import com.ediary.DTO.TopicDto;
import com.ediary.domain.Topic;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class TopicToTopicDto implements Converter<Topic, TopicDto> {

    @Nullable
    @Synchronized
    @Override
    public TopicDto convert(Topic source) {

        if (source == null) {
            return null;
        }

        final TopicDto topicDto = new TopicDto();

        topicDto.setId(source.getId());
        topicDto.setNumber(source.getNumber());
        topicDto.setName(source.getName());
        topicDto.setDescription(source.getDescription());

        //Subject
        topicDto.setSubjectId(source.getSubject().getId());
        topicDto.setSubjectName(source.getSubject().getName());

        return topicDto;
    }
}
