package com.ediary.repositories;

import com.ediary.domain.Class;
import com.ediary.domain.Subject;
import com.ediary.domain.Teacher;
import com.ediary.domain.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Teacher findByUserId(Long userId);
    Optional<Teacher> findByIdAndSchoolClassId(Long id, Long schoolClassId);
    Teacher findBySchoolClass(Class schoolClass);
    Optional<Teacher> findByUser(User user);
    List<Teacher> findAllBySchoolClassIsNullOrderByUser_LastName(Pageable pageable);
}
