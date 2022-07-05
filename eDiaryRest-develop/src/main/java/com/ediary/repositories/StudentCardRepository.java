package com.ediary.repositories;

import com.ediary.domain.Student;
import com.ediary.domain.StudentCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentCardRepository extends JpaRepository<StudentCard, Long> {
}
