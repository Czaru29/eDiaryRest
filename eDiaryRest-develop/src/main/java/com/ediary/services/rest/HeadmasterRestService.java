package com.ediary.services.rest;

import com.ediary.DTO.*;
import com.ediary.domain.helpers.Report;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface HeadmasterRestService {

    List<TeacherDto> listAllTeachers(Pageable pageable);
    List<Report> createTeacherReports(TeacherReportDtoRequest teacherReportDtoRequest);
    Boolean performYearClosing();
    List<EndYearReportDtoShort> listEndYearStudentsReports(String year, Pageable pageable);
    List<EndYearReportDtoShort> listEndYearTeachersReports(String year, Pageable pageable);
    List<String> getAllReportsYears();
    List<Report> getEndYearReportPdf(EndYearReportDtoRequest endYearReportDtoRequest);
}
