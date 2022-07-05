package com.ediary.repositories;

import com.ediary.domain.StudentCouncil;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentCouncilRepository extends JpaRepository<StudentCouncil, Long> {
    StudentCouncil findBySchoolClassId(Long classId);
}
