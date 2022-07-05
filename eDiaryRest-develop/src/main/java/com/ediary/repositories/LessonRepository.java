package com.ediary.repositories;

import com.ediary.domain.Lesson;
import com.ediary.domain.Subject;
import com.ediary.domain.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findAllBySubjectId(Long subjectId);
    Page<Lesson> findAllBySubjectIdOrderByDateDesc(Long subjectId, Pageable pageable);
    List<Lesson> findAllBySubject(Subject subject);
    List<Lesson> findAllBySubjectIdAndSchoolClassId(Long subjectId, Long classId);
    List<Lesson> findAllBySchoolClassId(Long classId);
    List<Lesson> findAllBySubjectAndDateAfterAndDateBefore(Subject subject, Date startTime, Date endTime);
    List<Lesson> findAllByTopic(Topic topic);
    Lesson findAllByDateAndSubjectId(Date date, Long subjectId);
}
