package com.ediary.services.rest;

import com.ediary.DTO.auth.TokenResponse;
import com.ediary.DTO.auth.UserCredentials;
import com.ediary.DTO.auth.UserPasswordChangeDto;
import com.ediary.domain.security.User;
import com.ediary.repositories.security.UserRepository;
import com.ediary.security.rest.JwtTokenUtil;
import com.ediary.util.UserDetailsHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenResponse getToken(UserCredentials userCredentials) {
        User user = (User) userDetailsService.loadUserByUsername(userCredentials.getUsername());
        if (passwordEncoder.matches(userCredentials.getPassword(), user.getPassword()))
            return new TokenResponse(jwtTokenUtil.generateAccessToken(user));
        return null;
    }

    @Override
    public TokenResponse changePassword(UserPasswordChangeDto userPasswordChangeDto) {
        User user = UserDetailsHelper.getCurrentUser();

        if (!passwordEncoder.matches(userPasswordChangeDto.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is not valid");
        }

        user.setPassword(passwordEncoder.encode(userPasswordChangeDto.getNewPassword()));

        User savedUser = userRepository.save(user);

        return new TokenResponse(jwtTokenUtil.generateAccessToken(savedUser));
    }
}
