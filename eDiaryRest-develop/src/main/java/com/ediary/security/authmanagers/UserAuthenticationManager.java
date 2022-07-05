package com.ediary.security.authmanagers;

import com.ediary.domain.security.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthenticationManager {

    public boolean userIdMatches(Authentication authentication, Long userId) {
        User authenticatedUser = (User) authentication.getPrincipal();

        return authenticatedUser.getId().equals(userId);
    }
}
