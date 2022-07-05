package com.ediary.web.controllers;

import com.ediary.DTO.SchoolClassDto;
import com.ediary.converters.UserToUserDto;
import com.ediary.domain.security.User;
import com.ediary.security.perms.DeputyHeadHeadmasterPermission;
import com.ediary.services.DeputyHeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.util.Optional;

@Profile("default")
@RequiredArgsConstructor
@Controller
@RequestMapping("/deputyHead")
public class DeputyHeadController {

    private final DeputyHeadService deputyHeadService;
    private final UserToUserDto userToUserDto;
    private final Integer pageSize = 15;

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
                setValue(LocalDate.parse(text));
            }
        });
    }

    @DeputyHeadHeadmasterPermission
    @GetMapping("/home")
    public String home() {

        return "deputyHead/index";
    }

    @DeputyHeadHeadmasterPermission
    @GetMapping("/newClass/formTutor")
    public String newClass(Model model,
                           @RequestParam(name = "page", required = false) Optional<Integer> page
                           ) {

        model.addAttribute("page", page);
        model.addAttribute("schoolClass", deputyHeadService.initNewClass());
        model.addAttribute("teachers", deputyHeadService.listAllTeachersWithoutClass(page.orElse(0),pageSize));

        return "deputyHead/newClass";
    }

    @DeputyHeadHeadmasterPermission
    @PostMapping("/newClass/formTutor")
    public String processNewClass(@ModelAttribute SchoolClassDto schoolClass, Model model,
                                  @RequestParam(name = "page", required = false) Optional<Integer> page) {

        model.addAttribute("page", page);
        model.addAttribute("schoolClass", schoolClass);
        model.addAttribute("selectedTeacherName", deputyHeadService.getSchoolClassWithTeacher(schoolClass.getTeacherId()));

        return "deputyHead/newClassName";
    }

    @DeputyHeadHeadmasterPermission
    @PostMapping("/newClass/name")
    public String processNewClassName(@Valid @ModelAttribute("schoolClass") SchoolClassDto schoolClass, BindingResult result,
                                      Model model) {

        if (result.hasErrors()) {
            return "deputyHead/newClassName";
        }

        if (deputyHeadService.schoolClassNameIsUnique(schoolClass.getName())) {
            deputyHeadService.saveSchoolClass(schoolClass);
        } else {
            model.addAttribute("uniqueName", Boolean.FALSE);
            model.addAttribute("selectedTeacherName", deputyHeadService.getSchoolClassWithTeacher(schoolClass.getTeacherId()));

            return "deputyHead/newClassName";
        }

        return "redirect:/deputyHead/classes";
    }


    @DeputyHeadHeadmasterPermission
    @GetMapping("/classes")
    public String getClasses(Model model) {

        model.addAttribute("classes", deputyHeadService.listAllSchoolClasses());

        return "deputyHead/classes";
    }

    @DeputyHeadHeadmasterPermission
    @GetMapping("/classes/{classId}")
    public String getOneClass(@PathVariable Long classId, Model model) {

        model.addAttribute("schoolClass", deputyHeadService.getSchoolClass(classId));

        return "deputyHead/oneClass";
    }

    @PostMapping("/classes/{classId}")
    public String deleteClass(@PathVariable Long classId) {

        deputyHeadService.deleteClass(classId);

        return "redirect:/deputyHead/classes";
    }

    @DeputyHeadHeadmasterPermission
    @GetMapping("/classes/{classId}/changeTeacher/{teacherId}")
    public String editTeacher(@PathVariable Long classId,
                              @PathVariable Long teacherId,
                              @RequestParam(name = "page", required = false) Optional<Integer> page,
                              Model model) {
        model.addAttribute("page", page);
        model.addAttribute("currentTeacher", deputyHeadService.getSchoolClassWithTeacher(teacherId, classId));
        model.addAttribute("teachers", deputyHeadService.listAllTeachersWithoutClass(page.orElse(0), pageSize));
        model.addAttribute("schoolClass", deputyHeadService.getSchoolClass(classId));


        return "deputyHead/editFormTutor";
    }

    @DeputyHeadHeadmasterPermission
    @PostMapping("/classes/{classId}/removeStudent/{studentId}")
    public String removeStudentFromClass(@PathVariable Long studentId,
                                         @PathVariable Long classId,
                                         @RequestParam(name = "page", required = false) Optional<Integer> page,
                                         @RequestParam(name = "addStudentsView", required = false) Boolean addStudentsView) {

        SchoolClassDto schoolClass = deputyHeadService.removeStudentFromClass(classId, studentId);


        if (addStudentsView != null && addStudentsView) {
            return "redirect:/deputyHead/classes/" + schoolClass.getId() + "/addStudents?page=" + page.orElse(0);
        }

        return "redirect:/deputyHead/classes/" + schoolClass.getId();
    }

    @DeputyHeadHeadmasterPermission
    @GetMapping("/classes/{classId}/addStudents")
    public String addStudents(@PathVariable Long classId,
                              @RequestParam(name = "page", required = false) Optional<Integer> page,
                              Model model) {


        model.addAttribute("page", page);
        model.addAttribute("studentsCount", deputyHeadService.countStudentsWithoutClass());
        model.addAttribute("students", deputyHeadService.listAllStudentsWithoutClass(page.orElse(0), pageSize));
        model.addAttribute("schoolClass", deputyHeadService.getSchoolClass(classId));

        return "deputyHead/addStudents";
    }

    @DeputyHeadHeadmasterPermission
    @PostMapping("/classes/{classId}/addTeacher/{teacherId}")
    public String addFormTutorToClass(@PathVariable Long teacherId,
                                      @PathVariable Long classId) {

        SchoolClassDto schoolClass = deputyHeadService.addFormTutorToClass(classId, teacherId);

        return "redirect:/deputyHead/classes/" + schoolClass.getId();
    }

    @DeputyHeadHeadmasterPermission
    @PostMapping("/classes/{classId}/addStudent/{studentId}")
    public String addStudentToClass(@PathVariable Long studentId,
                                    @PathVariable Long classId,
                                    @RequestParam(name = "page", required = false) Optional<Integer> page) {

        SchoolClassDto schoolClass = deputyHeadService.addStudentToClass(classId, studentId);


        return "redirect:/deputyHead/classes/" + schoolClass.getId() + "/addStudents?page=" + page.orElse(0);
    }


}
