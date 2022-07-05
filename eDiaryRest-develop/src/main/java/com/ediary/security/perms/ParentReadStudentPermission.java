package com.ediary.security.perms;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('PARENT') " +
        " AND @parentAuthenticationManager.parentIdAndStudentIdMatches(authentication, #parentId, #studentId)")
public @interface ParentReadStudentPermission {
}
