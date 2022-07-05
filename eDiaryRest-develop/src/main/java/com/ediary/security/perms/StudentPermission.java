package com.ediary.security.perms;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('STUDENT') " +
        " AND @studentAuthenticationManager.studentIdMatches(authentication, #studentId)")
public @interface StudentPermission {
}
