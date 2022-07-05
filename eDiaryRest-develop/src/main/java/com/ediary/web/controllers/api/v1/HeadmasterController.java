package com.ediary.web.controllers.api.v1;

import com.ediary.DTO.EndYearReportDtoRequest;
import com.ediary.DTO.EndYearReportDtoShort;
import com.ediary.DTO.TeacherDto;
import com.ediary.DTO.TeacherReportDtoRequest;
import com.ediary.domain.helpers.Report;
import com.ediary.security.perms.HeadmasterPermission;
import com.ediary.services.rest.HeadmasterRestService;
import com.ediary.util.ReportUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.ediary.web.controllers.api.v1.HeadmasterController.BASE_HEADMASTER_URL;

@Profile("rest")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = BASE_HEADMASTER_URL)
public class HeadmasterController {

    private final HeadmasterRestService headmasterRestService;

    public static final String BASE_HEADMASTER_URL = "/api/v1/headmaster";

    @GetMapping("/teacher-report/teachers")
    public ResponseEntity<List<TeacherDto>> getAllTeachers(Pageable pageable) {
        return ResponseEntity.ok(headmasterRestService.listAllTeachers(pageable));
    }

    @HeadmasterPermission
    @PostMapping(value = "/teacher-report", produces={"application/zip", "application/pdf"})
    public ResponseEntity<byte[]> generateTeacherReportPdf(@RequestBody TeacherReportDtoRequest teacherReportDtoRequest) {
        List<Report> teacherReports = headmasterRestService.createTeacherReports(teacherReportDtoRequest);
        return getReportResponseEntity(teacherReports);
    }

    @HeadmasterPermission
    @PostMapping("/year-closing")
    public ResponseEntity<?> processCloseYear() {
        headmasterRestService.performYearClosing();
        return ResponseEntity.ok().build();
    }

    @HeadmasterPermission
    @GetMapping("/end-year-reports/students")
    public ResponseEntity<List<EndYearReportDtoShort>> getAllEndYearReportsStudents(@RequestParam String year, Pageable pageable) {
        return ResponseEntity.ok(headmasterRestService.listEndYearStudentsReports(year, pageable));
    }

    @HeadmasterPermission
    @GetMapping("/end-year-reports/teachers")
    public ResponseEntity<List<EndYearReportDtoShort>> getAllEndYearReportsTeachers(@RequestParam String year, Pageable pageable) {
        return ResponseEntity.ok(headmasterRestService.listEndYearTeachersReports(year, pageable));
    }

    @HeadmasterPermission
    @GetMapping("/end-year-reports/years")
    public ResponseEntity<List<String>> getAllReportsYears() {
        return ResponseEntity.ok(headmasterRestService.getAllReportsYears());
    }

    @HeadmasterPermission
    @PostMapping(value = "/end-year-reports", produces={"application/zip", "application/pdf"})
    public ResponseEntity<byte[]> downloadEndYearReport(@RequestBody @Valid EndYearReportDtoRequest endYearReportDtoRequest) {
        List<Report> endYearReports = headmasterRestService.getEndYearReportPdf(endYearReportDtoRequest);
        return getReportResponseEntity(endYearReports);
    }

    @NotNull
    private ResponseEntity<byte[]> getReportResponseEntity(List<Report> endYearReports) {
        HttpHeaders httpHeaders = new HttpHeaders();

        if (endYearReports.size() == 1) {
            httpHeaders.setContentType(MediaType.APPLICATION_PDF);
            return new ResponseEntity<>(endYearReports.get(0).getFile(), httpHeaders, HttpStatus.OK);
        }

        httpHeaders.setContentType(MediaType.parseMediaType("application/zip"));
        return new ResponseEntity<>(ReportUtil.convertToZip(endYearReports), httpHeaders, HttpStatus.OK);
    }
}
