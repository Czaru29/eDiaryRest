package com.ediary.DTO.auth;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCredentials {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
