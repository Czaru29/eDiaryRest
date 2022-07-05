package com.ediary.services.rest;

import com.ediary.DTO.auth.TokenResponse;
import com.ediary.DTO.auth.UserCredentials;
import com.ediary.DTO.auth.UserPasswordChangeDto;

public interface AuthService {

    TokenResponse getToken(UserCredentials userCredentials);

    TokenResponse changePassword(UserPasswordChangeDto userPasswordChangeDto);
}
