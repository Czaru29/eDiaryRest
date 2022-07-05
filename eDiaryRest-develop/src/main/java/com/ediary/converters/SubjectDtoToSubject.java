package com.ediary.converters;

import com.ediary.DTO.SubjectDto;
import com.ediary.domain.Class;
import com.ediary.domain.Subject;
import com.ediary.domain.Teacher;
import com.ediary.repositories.ClassRepository;
import com.ediary.repositories.LessonRepository;
import com.ediary.repositories.TeacherRepository;
import com.ediary.repositories.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class SubjectDtoToSubject implements Converter<SubjectDto, Subject> {


    private final TeacherRepository teacherRepository;
    private final LessonRepository lessonRepository;
    private final ClassRepository classRepository;
    private final TopicRepository topicRepository;

    @Nullable
    @Synchronized
    @Override
    public Subject convert(SubjectDto source) {

        if(source == null){
            return null;
        }

        final Subject subject = new Subject();

        subject.setId(source.getId());
        subject.setName(source.getName());

        //Teachers
        Optional<Teacher> teacherOptional = teacherRepository.findById(source.getTeacherId());
        if (teacherOptional.isPresent()) {
            subject.setTeacher(teacherOptional.get());
        }

        //Class
        if (source.getClassId() != null) {
            Optional<Class> classOptional = classRepository.findById(source.getClassId());
            if (classOptional.isPresent()) {
                subject.setSchoolClass(classOptional.get());
            }
        }

        //Lessons
        if (source.getLessonsId() != null) {
            subject.setLessons(new HashSet<>(lessonRepository.findAllById(source.getLessonsId())));
        }
        //Topic
        if (source.getTopicsId() != null) {
            subject.setTopics(new HashSet<>(topicRepository.findAllById(source.getTopicsId())));
        }
        return subject;
    }
}
