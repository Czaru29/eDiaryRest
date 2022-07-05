package com.ediary.web.controllers;

import com.ediary.DTO.RoleDto;
import com.ediary.DTO.SchoolDto;
import com.ediary.DTO.UserDto;
import com.ediary.converters.UserToUserDto;
import com.ediary.domain.security.User;
import com.ediary.security.perms.AdminPermission;
import com.ediary.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    private final UserToUserDto userToUserDto;

    @ModelAttribute
    public void addAuthenticatedUserAndParent(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userToUserDto.convert(user));
    }


    @AdminPermission
    @GetMapping("/home")
    public String home() {
        return "admin/index";
    }


    @AdminPermission
    @GetMapping("/users")
    public String getAllUsers(Model model) {
        model.addAttribute("users", adminService.getAllUsers());

        return "admin/allUsers";
    }

    @AdminPermission
    @GetMapping("/newUser")
    public String initNewUser(Model model) {

        model.addAttribute("newUser", adminService.initNewUser());
        model.addAttribute("roles", adminService.getAllRoles());
        model.addAttribute("students", adminService.getAllStudentsWithoutParent());

        return "admin/newUser";
    }

    @AdminPermission
    @PostMapping("/newUser")
    public String processNewUser(@Valid @ModelAttribute("newUser") UserDto userDto,
                                 BindingResult result,
                                 @RequestParam(name = "selectedRoles") List<Long> rolesId,
                                 @RequestParam(name = "selectedStudents", required = false) List<Long> selectedStudentsForParent,
                                 Model model) {

        if (result.hasErrors()) {
            model.addAttribute("roles", adminService.getAllRoles());
            model.addAttribute("students", adminService.getAllStudentsWithoutParent());

            return "admin/newUser";
        }

        User user = adminService.saveUser(userDto, rolesId, selectedStudentsForParent);

        return "redirect:/admin/users";
    }

    @AdminPermission
    @GetMapping("/users/{userId}")
    public String getUser(@PathVariable Long userId, Model model) {

        model.addAttribute("user", adminService.getUser(userId));

        return "admin/user";
    }


    @AdminPermission
    @PostMapping("/users/{userId}/delete")
    public String deleteUser(@PathVariable Long userId) {

        adminService.deleteUser(userId);

        return "redirect:/admin/users";
    }

    @AdminPermission
    @GetMapping("/users/{userId}/edit")
    public String editUser(@PathVariable Long userId, Model model) {

        model.addAttribute("editUser", adminService.getUser(userId));
        model.addAttribute("roles", adminService.getAllRoles());
        model.addAttribute("ownedRoles", adminService.getUser(userId).getRoles().stream().map(RoleDto::getName).collect(Collectors.toList()));
        model.addAttribute("students", adminService.getAllStudentsWithoutParent());

        return "admin/editUser";
    }

    @AdminPermission
    @PostMapping("/users/{userId}/role/delete")
    public String deleteRole(@PathVariable Long userId,
                             @RequestParam(name = "roleToDelete") String role) {

        adminService.deleteRole(userId, role);

        return "redirect:/admin/users/" + userId + "/edit";
    }

    @AdminPermission
    @PostMapping("/users/{userId}/update")
    public String updateUser(@PathVariable Long userId,
                             @RequestParam(name = "selectedRoles", required = false) List<Long> rolesId,
                             @RequestParam(name = "selectedStudents", required = false) List<Long> selectedStudentsForParent,
                             @Valid @ModelAttribute("editUser") UserDto userDto,
                             BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("roles", adminService.getAllRoles());
            model.addAttribute("ownedRoles", adminService.getUser(userId).getRoles().stream().map(RoleDto::getName).collect(Collectors.toList()));
            model.addAttribute("students", adminService.getAllStudentsWithoutParent());
            userDto.setId(userId);
            return "admin/editUser";
        }

        userDto.setId(userId);
        UserDto updatedUser = adminService.updateUser(userDto, rolesId, selectedStudentsForParent);

        return "redirect:/admin/users/";
    }

    @AdminPermission
    @GetMapping("/school")
    public String getSchoolInfo(Model model) {

        model.addAttribute("school", adminService.getSchool());

        return "admin/school";
    }

    @AdminPermission
    @GetMapping("/school/edit")
    public String editSchool(Model model) {
        model.addAttribute("school", adminService.getSchool());

        return "admin/editSchool";
    }

    @AdminPermission
    @PostMapping("/school/{schoolId}/{addressId}/update")
    public String updateSchool(@PathVariable Long schoolId,
                               @PathVariable Long addressId,
                               @Valid @ModelAttribute("school") SchoolDto schoolDto,
                               BindingResult result) {

        if (result.hasErrors()) {
            return "admin/school";
        }
        schoolDto.setId(schoolId);
        schoolDto.getAddressDto().setId(addressId);
        adminService.updateSchool(schoolDto);

        return "redirect:/admin/school";
    }

}
