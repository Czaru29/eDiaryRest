package com.ediary.repositories;

import com.ediary.domain.Grade;
import com.ediary.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findAllByStudentId(Long studentId);
    List<Grade> findAllByStudentIdAndDateAndSubjectId(Long studentId, Date date, Long subjectId);
    List<Grade> findAllByStudentIdAndSubjectId(Long studentId, Long subjectId);
    List<Grade> findAllByStudentIdAndSubjectIdAndWeightNotIn(Long studentId, Long subjectId, List<Integer> weight);
    List<Grade> findAllByTeacherIdAndSubjectIdAndStudentIdAndWeightNotIn(Long teacherId, Long subjectId, Long studentId, List<Integer> weight);
    List<Grade> findAllBySubjectIdAndStudentIdAndWeightNotIn(Long subjectId, Long studentId, List<Integer> weight);
    Grade findByTeacherIdAndSubjectIdAndStudentIdAndWeight(Long teacherId, Long subjectId, Long studentId, Integer weight);
    Grade findBySubjectIdAndStudentIdAndWeight(Long subjectId, Long studentId, Integer weight);
    Grade findByStudentIdAndWeight(Long studentId, Integer weight);
    List<Grade> findAllByStudentIdAndSubjectIdAndDateAfterAndDateBefore(Long studentId, Long subjectId,
                                                                        java.util.Date startDate, java.util.Date endDate);
    List<Grade> findAllByTeacherIdAndDateAfterAndDateBefore(Long teacherId, Date startTime, Date endTime);
    List<Grade> findAllBySubjectIdAndTeacherId(Long subjectId, Long teacherId);
    List<Grade> findAllByTeacherIdAndWeight(Long teacherId, Integer weight);
    List<Grade> findAllBySubject(Subject subject);
    List<Grade> findAllByTeacherIdAndDate(Long teacherId, Date date);

    Long countAllByStudentIdAndDate(Long studentId, Date date);
    Long countAllByTeacherIdAndSubjectIdAndStudentId(Long teacherId, Long subjectId, Long studentId);
    Long countAllBySubjectIdAndStudentId(Long subjectId, Long studentId);

}
