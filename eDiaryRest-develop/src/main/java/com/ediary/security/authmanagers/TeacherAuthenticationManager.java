package com.ediary.security.authmanagers;

import com.ediary.domain.security.User;
import com.ediary.repositories.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherAuthenticationManager {

    private final TeacherRepository teacherRepository;

    public Boolean teacherIdMatches(Authentication authentication, Long teacherId) {
        User authenticatedUser = (User) authentication.getPrincipal();

        return teacherRepository.findByUserId(authenticatedUser.getId()).getId().equals(teacherId);
    }
}
