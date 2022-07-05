package com.ediary.repositories;

import com.ediary.domain.Behavior;
import com.ediary.domain.Teacher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BehaviorRepository extends JpaRepository<Behavior, Long> {

    List<Behavior> findAllByStudentId(Long studentId);
    List<Behavior> findAllByStudentIdOrderByDateDesc(Long studentId, Pageable pageable);
    List<Behavior> findAllByTeacherOrderByDateDesc(Teacher teacher);
    List<Behavior> findAllByTeacherOrderByDateDesc(Teacher teacher, Pageable pageable);
    Long countAllByStudentIdAndPositive(Long studentId, boolean positive);
}
