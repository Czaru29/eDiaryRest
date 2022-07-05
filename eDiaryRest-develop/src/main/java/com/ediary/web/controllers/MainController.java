package com.ediary.web.controllers;

import com.ediary.converters.UserToUserDto;
import com.ediary.domain.security.User;
import com.ediary.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final UserToUserDto userToUserDto;
    private final AdminService adminService;

    @GetMapping("/")
    public String main(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userToUserDto.convert(user));
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("school", adminService.getSchool());
        return "login";
    }
    @GetMapping("/logout")
    public String logout(Model model) {
        model.addAttribute("school", adminService.getSchool());
        return "logout";
    }
}
