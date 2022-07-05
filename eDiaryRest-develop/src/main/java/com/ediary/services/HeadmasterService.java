package com.ediary.services;

import com.ediary.DTO.EndYearReportDto;
import com.ediary.DTO.TeacherDto;
import com.ediary.domain.Report;
import com.ediary.domain.helpers.TimeInterval;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface HeadmasterService {

    Report saveReport(Report report);

    List<TeacherDto> listAllTeachers(Integer page, Integer size);
    TimeInterval initNewTimeInterval();
    TimeInterval setTimeInterval(LocalDate startTime, LocalDate endTime);

    Boolean createTeacherReport(HttpServletResponse response, Long teacherId, Date startTime, Date endTime) throws Exception;
    Boolean performYearClosing();

    List<EndYearReportDto> listEndYearStudentsReports(Integer page, Integer size);
    List<EndYearReportDto> listEndYearTeachersReports(Integer page, Integer size);
    boolean getEndYearReportPdf(HttpServletResponse response, Long reportId) throws IOException;
}
