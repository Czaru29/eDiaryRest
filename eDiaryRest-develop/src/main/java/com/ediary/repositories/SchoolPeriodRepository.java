package com.ediary.repositories;

import com.ediary.domain.SchoolPeriod;
import com.ediary.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchoolPeriodRepository extends JpaRepository<SchoolPeriod, Long> {

    List<SchoolPeriod> findAllBySchoolClassId(Long classId);
    List<SchoolPeriod> findAllBySubject(Subject subject);
    List<SchoolPeriod> findAllByTeacherId(Long teacherId);
}
