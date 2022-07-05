package com.ediary.security.authmanagers;

import com.ediary.domain.security.User;
import com.ediary.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentAuthenticationManager {

    private final StudentRepository studentRepository;

    public boolean studentIdMatches(Authentication authentication, Long studentId) {
        User authenticatedUser = (User) authentication.getPrincipal();

        return studentRepository.findByUserId(authenticatedUser.getId()).getId().equals(studentId);
    }
}
