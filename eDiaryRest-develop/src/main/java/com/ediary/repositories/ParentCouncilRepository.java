package com.ediary.repositories;

import com.ediary.domain.ParentCouncil;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentCouncilRepository extends JpaRepository<ParentCouncil, Long> {
    ParentCouncil findBySchoolClassId(Long classId);
}
