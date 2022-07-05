package com.ediary.repositories;

import com.ediary.domain.Attendance;
import com.ediary.domain.Lesson;
import com.ediary.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findAllByStudentId(Long studentId);
    Attendance findByStudent(Student student);
    Attendance findByStudentIdAndLessonId(Long studentId, Long lessonId);
    Set<Attendance> findAllByLesson_Id(Long lessonId);
    List<Attendance> findAllByStudentIdAndLesson_Date(Long studentId, Date date);
    List<Attendance> findAllByStudentIdAndLesson_DateBetween(Long studentId, Date startDate, Date endDate);
    Attendance findDistinctByStudentIdAndLesson(Long studentId, Lesson lesson);
    Attendance findDistinctByStudentIdAndLessonId(Long studentId, Long lessonId);
    Long countAllByStudentIdAndStatusIn(Long studentId, List<Attendance.Status> status);

}
