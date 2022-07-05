package com.ediary.security.authmanagers;

import com.ediary.domain.Parent;
import com.ediary.domain.Student;
import com.ediary.domain.security.User;
import com.ediary.exceptions.NotFoundException;
import com.ediary.repositories.ParentRepository;
import com.ediary.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParentAuthenticationManager {

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;

    public boolean parentIdMatches(Authentication authentication, Long parentId) {
        User authenticatedUser = (User) authentication.getPrincipal();

        return parentRepository.findByUserId(authenticatedUser.getId()).getId().equals(parentId);
    }


    public boolean parentIdAndStudentIdMatches(Authentication authentication, Long parentId, Long studentId) {
        User authenticatedUser = (User) authentication.getPrincipal();

        Parent parent = parentRepository.findByUserId(authenticatedUser.getId());

        if (parent == null || !authenticatedUser.getId().equals(parent.getUser().getId())) {
            return false;
        }

        if (parent.getStudents() != null) {
            Student student = studentRepository.findById(studentId).orElseThrow(() -> new NotFoundException("Student not found"));

            return parent.getStudents().contains(student);
        }

        return false;
    }
}
