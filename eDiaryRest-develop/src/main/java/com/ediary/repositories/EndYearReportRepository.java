package com.ediary.repositories;

import com.ediary.domain.EndYearReport;
import com.ediary.domain.Student;
import com.ediary.domain.Teacher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EndYearReportRepository  extends JpaRepository<EndYearReport, Long> {

    List<EndYearReport> findAllByUserTypeOrderByIdDesc(EndYearReport.Type type, Pageable pageable);
    List<EndYearReport> findAllByUserTypeAndYearOrderByIdDesc(EndYearReport.Type type, String year, Pageable pageable);
    Optional<EndYearReport> findByIdAndStudent(Long reportId, Student student);
    Optional<EndYearReport> findByIdAndTeacher(Long reportId, Teacher teacher);

    @Query("select distinct r.year from EndYearReport r")
    List<String> findAllYears();
}
