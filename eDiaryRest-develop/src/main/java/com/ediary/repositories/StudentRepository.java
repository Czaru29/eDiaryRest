package com.ediary.repositories;

import com.ediary.domain.Student;
import com.ediary.domain.security.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findAllByParentId(Long parentId);
    List<Student> findAllByParentIsNull();
    Optional<Student> findById(Long studentId);
    Student findByUserId(Long userId);
    Optional<Student> findByUser(User user);
    List<Student> findAllBySchoolClassId(Long schoolClassId);
    List<Student> findAllBySchoolClassIdOrderByUserLastName(Long schoolClassId);
    Page<Student> findAllBySchoolClassIsNull(Pageable pageable);
    Long countStudentBySchoolClassIsNull();
}
