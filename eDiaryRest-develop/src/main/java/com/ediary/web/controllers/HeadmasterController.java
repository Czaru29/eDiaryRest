package com.ediary.web.controllers;

import com.ediary.converters.UserToUserDto;
import com.ediary.domain.security.User;
import com.ediary.security.perms.HeadmasterPermission;
import com.ediary.security.perms.StudentPermission;
import com.ediary.services.HeadmasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyEditorSupport;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Optional;

@Profile("default")
@RequiredArgsConstructor
@Controller
@RequestMapping("/headmaster")
public class HeadmasterController {

    private final HeadmasterService headmasterService;
    private final UserToUserDto userToUserDto;

    @ModelAttribute
    public void addAuthenticatedUser(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userToUserDto.convert(user));
    }

    @InitBinder
    public void dataBinder(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");

        dataBinder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text.length() < 10) {
                    text = "01/" + text;
                }

                DateTimeFormatter f = new DateTimeFormatterBuilder().parseCaseInsensitive()
                        .append(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toFormatter();
                setValue(LocalDate.parse(text, f));
            }


        });
    }

    @HeadmasterPermission
    @GetMapping("/home")
    public String home() {
        return "headmaster/index";
    }


    @HeadmasterPermission
    @GetMapping("/teacherReport")
    public String getTeacherReport(Model model,
                                   @RequestParam(name = "page", required = false) Optional<Integer> page) {

        model.addAttribute("teachers", headmasterService.listAllTeachers(page.orElse(0), 20));
        model.addAttribute("page", page);
        model.addAttribute("timeInterval", headmasterService.initNewTimeInterval());

        return "headmaster/teacherReport";
    }

    @HeadmasterPermission
    @PostMapping("/teacherReport")
    public String processNewTimeInterval(Model model,
                                         @RequestParam(name = "page", required = false) Optional<Integer> page,
                                         @RequestParam(name = "startTime") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startTime,
                                         @RequestParam(name = "endTime") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endTime) {

        model.addAttribute("teachers", headmasterService.listAllTeachers(page.orElse(0), 20));
        model.addAttribute("page", page);

        if (startTime.isAfter(endTime)) {
            model.addAttribute("timeInterval", headmasterService.initNewTimeInterval());
            model.addAttribute("invalidDates", Boolean.TRUE);

            return "headmaster/teacherReport";
        }

        model.addAttribute("timeInterval", headmasterService.setTimeInterval(startTime, endTime));

        return "headmaster/teacherReport";
    }

    @HeadmasterPermission
    @RequestMapping("/teacherReport/{teacherId}/download/{startTime}/{endTime}")
    public void downloadStudentCardPdf(HttpServletResponse response,
                                       @PathVariable Date startTime,
                                       @PathVariable Date endTime,
                                       @RequestHeader String referer,
                                       @PathVariable Long teacherId) throws Exception {

        //Not allowing to download via url typing
        if (referer != null && !referer.isEmpty()) {
        }

        headmasterService.createTeacherReport(response, teacherId, startTime, endTime);

    }

    @HeadmasterPermission
    @GetMapping("/closeYear")
    public String closeYear(Model model) {
        return "headmaster/closeYear";
    }

    @HeadmasterPermission
    @PostMapping("/closeYear")
    public String processCloseYear(Model model) {

        Boolean result = headmasterService.performYearClosing();
        model.addAttribute("result", result);
        return "headmaster/closeYear";
    }

    @HeadmasterPermission
    @GetMapping("/endYearReports")
    public String getAllEndYearReports(Model model) {
        return "headmaster/endYearReports";
    }

    @HeadmasterPermission
    @GetMapping("/endYearReports/students")
    public String getAllEndYearReportsStudents(Model model,
                                               @RequestParam(name = "page", required = false) Optional<Integer> page) {

        model.addAttribute("page", page);
        model.addAttribute("reports", headmasterService.listEndYearStudentsReports(page.orElse(0), 15));

        return "headmaster/endYearReportsStudents";
    }

    @HeadmasterPermission
    @GetMapping("/endYearReports/teachers")
    public String getAllEndYearReportsTeachers(Model model,
                                               @RequestParam(name = "page", required = false) Optional<Integer> page) {

        model.addAttribute("page", page);
        model.addAttribute("reports", headmasterService.listEndYearTeachersReports(page.orElse(0), 15));

        return "headmaster/endYearReportsTeachers";
    }

    @HeadmasterPermission
    @RequestMapping("/endYearReports/{reportId}")
    public void downloadEndYearReport(HttpServletResponse response, @PathVariable Long reportId) {
        try {
            headmasterService.getEndYearReportPdf(response, reportId);
        } catch (Exception e) {

        }
    }


}
