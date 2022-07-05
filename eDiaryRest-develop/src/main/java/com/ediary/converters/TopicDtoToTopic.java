package com.ediary.converters;

import com.ediary.DTO.TopicDto;
import com.ediary.domain.Topic;
import com.ediary.repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TopicDtoToTopic implements Converter<TopicDto, Topic> {

    private final SubjectRepository subjectRepository;

    @Nullable
    @Synchronized
    @Override
    public Topic convert(TopicDto source) {

        if (source == null) {
            return null;
        }

        final Topic topic = new Topic();
        topic.setId(source.getId());
        topic.setNumber(source.getNumber());
        topic.setName(source.getName());
        topic.setDescription(source.getDescription());

        //Subject
        topic.setSubject(subjectRepository.findById(source.getSubjectId()).orElse(null));

        return topic;
    }

    @Nullable
    @Synchronized
    public Topic convertForSave(TopicDto source) {

        if (source == null) {
            return null;
        }

        final Topic topic = new Topic();

        topic.setNumber(source.getNumber());
        topic.setName(source.getName());
        topic.setDescription(source.getDescription());

        topic.setSubject(subjectRepository.findById(source.getSubjectId()).orElse(null));

        return topic;
    }

}
