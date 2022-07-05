package com.ediary.converters;

import com.ediary.DTO.LessonDto;
import com.ediary.domain.Class;
import com.ediary.domain.Lesson;
import com.ediary.domain.Subject;
import com.ediary.domain.Topic;
import com.ediary.repositories.AttendanceRepository;
import com.ediary.repositories.ClassRepository;
import com.ediary.repositories.SubjectRepository;
import com.ediary.repositories.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class LessonDtoToLesson implements Converter<LessonDto, Lesson> {

    private final ClassRepository classRepository;
    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;

    @Synchronized
    @Nullable
    @Override
    public Lesson convert(LessonDto source) {

        if(source == null){
            return null;
        }

        final Lesson lesson = new Lesson();
        lesson.setId(source.getId());
        lesson.setName(source.getName());
        lesson.setDate(source.getDate());

        //Class
        Optional<Class> classOptional = classRepository.findById(source.getClassId());
        if (classOptional.isPresent()) {
            lesson.setSchoolClass(classOptional.get());
        }

        //Topic
        Optional<Topic> topicOptional = topicRepository.findById(source.getTopicId());
        if (topicOptional.isPresent()) {
            lesson.setTopic(topicOptional.get());
        }

        //Subject
        Optional<Subject> subjectOptional = subjectRepository.findById(source.getSubjectId());
        if (subjectOptional.isPresent()) {
            lesson.setSubject(subjectOptional.get());
        }

        //Attendance
        lesson.setAttendances(attendanceRepository.findAllByLesson_Id(lesson.getId()));

        return lesson;
    }
}
