package com.ediary.web.controllers;

import com.ediary.DTO.*;
import com.ediary.converters.UserToUserDto;
import com.ediary.domain.Extenuation;
import com.ediary.domain.Student;
import com.ediary.domain.security.User;
import com.ediary.security.perms.ParentReadStudentPermission;
import com.ediary.security.perms.StudentPermission;
import com.ediary.services.ParentService;
import com.ediary.services.StudentService;
import com.ediary.services.SubjectService;
import com.ediary.services.WeeklyAttendancesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/parent/{parentId}/")
public class ParentController {

    private final ParentService parentService;
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final WeeklyAttendancesService weeklyAttendancesService;

    private final UserToUserDto userToUserDto;

    @ModelAttribute
    public void addAuthenticatedUserAndParent(@AuthenticationPrincipal User user,
                                              @CookieValue(value = "studentId", defaultValue = "NaN") String studentIdCookie,
                                              Model model) {
        model.addAttribute("user", userToUserDto.convert(user));
        model.addAttribute("parent", parentService.findByUser(user));

        try {
            Long studentId = Long.parseLong(studentIdCookie);
            StudentDto student = parentService.findStudent(user, studentId);
            model.addAttribute("student", student);
        } catch (Exception e) {

        }
    }

    @InitBinder
    public void dataBinder(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");

        dataBinder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(LocalDate.parse(text));
            }
        });
    }

    @PreAuthorize("hasRole('PARENT')")
    @GetMapping("/home")
    public String home() {

        return "/parent/index";
    }

    @PreAuthorize("hasRole('PARENT')")
    @GetMapping("/home/student")
    public String homeStudent() {

        return "/parent/indexStudent";
    }

    @PreAuthorize("hasRole('PARENT')")
    @GetMapping("/students")
    public String getAllStudents(@PathVariable Long parentId, Model model) {

        model.addAttribute("students", parentService.listStudents(parentId));

        return "parent/allStudents";
    }

    @ParentReadStudentPermission
    @GetMapping("/{studentId}")
    public String getStudentIndex(@PathVariable Long parentId, @PathVariable Long studentId, Model model) {

        model.addAttribute("students", parentService.findStudent(parentId, studentId));

        return "parent/index";
    }

    @ParentReadStudentPermission
    @GetMapping("/{studentId}/grade")
    public String getAllGrades(@PathVariable Long studentId, @PathVariable Long parentId, Model model) {

        model.addAttribute("subjectsGrades", studentService.listSubjectsGrades(studentId));
        model.addAttribute("behaviorGrade", studentService.getBehaviorGrade(studentId));
        model.addAttribute("student", parentService.findStudent(parentId, studentId));

        return "parent/allGrades";
    }

    @ParentReadStudentPermission
    @GetMapping("/{studentId}/grade/subject/{subjectId}")
    public String getAllGradesBySubject(@PathVariable Long studentId,
                                        @PathVariable Long subjectId, Model model) {

        model.addAttribute("subjectName", subjectService.getNameById(subjectId));
        model.addAttribute("grades", studentService.listGrades(studentId, subjectId));
        return "parent/allGradesBySubject";
    }

    @ParentReadStudentPermission
    @GetMapping("/{studentId}/attendance")
    public String getAllAttendances(@PathVariable Long studentId, @PathVariable Long parentId, Model model) {

        model.addAttribute("weeklyAttendances",
                weeklyAttendancesService.getAttendancesByWeek(studentId, 7, Date.valueOf(LocalDate.now().minusDays(6))));
        model.addAttribute("studentId", studentId);
        return "parent/allAttendances";
    }

    @ParentReadStudentPermission
    @GetMapping("/{studentId}/attendance/{direction}/{dateValue}")
    public String getAllAttendancesWithDate(@PathVariable Long studentId,
                                            @PathVariable Long parentId,
                                            @PathVariable String direction,
                                            @PathVariable String dateValue,
                                            Model model) {

        Date date = Date.valueOf(LocalDate.now().minusDays(6));
        try {
            if (direction.equals("next")) {
                date = Date.valueOf(LocalDate.parse(dateValue).plusDays(7));
            } else {
                date = Date.valueOf(LocalDate.parse(dateValue).minusDays(7));
            }
        } catch (DateTimeParseException e) {

        }

        model.addAttribute("weeklyAttendances",
                weeklyAttendancesService.getAttendancesByWeek(studentId, 7, date));
        model.addAttribute("studentId", studentId);

        return "parent/allAttendances";
    }

    @ParentReadStudentPermission
    @PostMapping("/{studentId}/attendance/extenuation")
    public String newExtenuation(@PathVariable Long parentId,
                                 @PathVariable Long studentId,
                                 @ModelAttribute ExtenuationDto extenuation,
                                 @RequestParam(name = "toExcuse", required = false) List<Long> ids,
                                 Model model) {

        model.addAttribute("studentId", studentId);
        model.addAttribute("extenuation", parentService.initNewExtenuation(ids, extenuation, parentId));
        return "parent/newExtenuation";
    }

    @ParentReadStudentPermission
    @PostMapping("/{studentId}/attendance/extenuation/save")
    public String processNewExtenuation(@PathVariable Long studentId, @PathVariable Long parentId,
                                        @Valid @ModelAttribute("extenuation") ExtenuationDto extenuation,
                                        BindingResult result,
                                        @RequestParam(name = "attId", required = false) List<Long> ids, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("extenuation", parentService.initNewExtenuation(ids, extenuation, parentId));
            model.addAttribute("error", Boolean.TRUE);
            return "parent/newExtenuation";
        }

        Extenuation ext = parentService.saveExtenuation(extenuation, parentId, ids);

        return "redirect:/parent/" + parentId + "/" + studentId + "/attendance/extenuations";
    }

    @ParentReadStudentPermission
    @GetMapping("/{studentId}/attendance/extenuations")
    public String getAllExtenuations(@PathVariable Long parentId, @PathVariable Long studentId, Model model) {

        model.addAttribute("extenuations", parentService.getAllExtenuations(parentId));

        return "parent/allExtenuations";
    }

    @ParentReadStudentPermission
    @GetMapping("/{studentId}/behavior")
    public String getAllBehaviors(@PathVariable Long studentId,
                                  @RequestParam(name = "page", required = false) Optional<Integer> page,
                                  @PathVariable Long parentId, Model model) {

        model.addAttribute("page", page);
        model.addAttribute("behaviors", studentService.listBehaviors(studentId,  page.orElse(0), 10));
        return "parent/allBehaviors";
    }

    @ParentReadStudentPermission
    @GetMapping("/{studentId}/event")
    public String getAllEvents(@PathVariable Long studentId,
                               @PathVariable Long parentId,
                               @RequestParam(name = "page", required = false) Optional<Integer> page,
                               Model model) {

        
        model.addAttribute("page", page);
        model.addAttribute("events", studentService.listEvents(studentId, page.orElse(0), 10, false));
        return "parent/allEvents";
    }

    @ParentReadStudentPermission
    @GetMapping("/{studentId}/event/eventsHistory")
    public String getAllEventsHistory(@PathVariable Long studentId,
                                      @PathVariable Long parentId,
                                      @RequestParam(name = "page", required = false) Optional<Integer> page,
                                      Model model) {

        model.addAttribute("page", page);
        model.addAttribute("events", studentService.listEvents(studentId, page.orElse(0), 10, true));
        return "parent/allEventsHistory";
    }

    @ParentReadStudentPermission
    @GetMapping("/{studentId}/timetable")
    public String getTimetable(@PathVariable Long studentId, @PathVariable Long parentId, Model model) {

        model.addAttribute("timetable", studentService.getTimetableByStudentId(studentId));
        return "parent/timetable";
    }

    @ParentReadStudentPermission
    @GetMapping("/{studentId}/endYearReports")
    public String getAllEndYearReports(@PathVariable Long studentId, Model model) {

        model.addAttribute("reports", studentService.listEndYearReports(studentId));
        return "parent/endYearReports";
    }

    @ParentReadStudentPermission
    @RequestMapping("/{studentId}/endYearReports/{reportId}")
    public void downloadEndYearReport(HttpServletResponse response,
                                      @PathVariable Long parentId,
                                      @PathVariable Long studentId,
                                      @PathVariable Long reportId) {

        try {
            studentService.getEndYearReportPdf(response, studentId, reportId);
        } catch (Exception e) {

        }

    }



}
