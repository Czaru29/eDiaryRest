package com.ediary.web.controllers.api.v1;

import com.ediary.DTO.auth.TokenResponse;
import com.ediary.DTO.auth.UserCredentials;
import com.ediary.DTO.auth.UserPasswordChangeDto;
import com.ediary.services.rest.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.Objects;

import static com.ediary.web.controllers.api.v1.AuthController.BASE_AUTH_URL;

@Profile("rest")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = BASE_AUTH_URL)
public class AuthController {

    private final AuthService authService;

    public static final String BASE_AUTH_URL = "/api/v1/auth/";

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserCredentials userCredentials) {
        TokenResponse token = authService.getToken(userCredentials);
        if (Objects.nonNull(token)) {
            return ResponseEntity.ok(token);
        } else {
            return new ResponseEntity<>("Login error", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password-change")
    public ResponseEntity<TokenResponse> changeUserPassword(@Valid @RequestBody UserPasswordChangeDto userPasswordChangeDto) {
        return ResponseEntity.ok(authService.changePassword(userPasswordChangeDto));
    }
}
