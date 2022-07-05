package com.ediary.security.perms;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('FORM_TUTOR')" +
        " AND @teacherAuthenticationManager.teacherIdMatches(authentication, #teacherId)")
public @interface FormTutorPermission {
}
