package com.ediary.repositories;

import com.ediary.domain.Parent;
import com.ediary.domain.Student;
import com.ediary.domain.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    Parent findByUserId(Long userId);
    Optional<Parent> findByUser(User user);
    List<Parent> findAllByStudentsIn(Set<Student> students);
    List<Parent> findAllByStudentsInOrderByUserLastName(Set<Student> students);
}
