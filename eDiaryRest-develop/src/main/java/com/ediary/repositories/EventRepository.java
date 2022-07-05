package com.ediary.repositories;

import com.ediary.domain.Class;
import com.ediary.domain.Event;
import com.ediary.domain.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllBySchoolClass(Class schoolClass);
    List<Event> findAllBySchoolClassId(Long classId);
    Page<Event> findAllBySchoolClassIdOrderByDate(Long schoolClassId, Pageable pageable);
    Page<Event> findAllBySchoolClassIdAndDateBefore(Long schoolClassId, Date date, Pageable pageable);
    Page<Event> findAllBySchoolClassIdAndDateAfter(Long schoolClassId, Date date, Pageable pageable);
    List<Event> findAllByTeacher(Teacher teacher);
    List<Event> findAllByTeacherIdAndDateAfterAndDateBefore(Long teacherId, Date startTime, Date endTime);
    List<Event> findAllByTeacherIdAndSchoolClassIdAndDateAfter(Long teacherId, Long classId, Date date, Pageable pageable);
    List<Event> findAllByTeacherIdAndSchoolClassIdAndDateBefore(Long teacherId, Long classId, Date date, Pageable pageable);
    List<Event> findAllByTeacherIdAndDateBefore(Long teacherId, Date date, Pageable pageable);
    List<Event> findAllByTeacherIdAndDateAfter(Long teacherId, Date date, Pageable pageable);
}
