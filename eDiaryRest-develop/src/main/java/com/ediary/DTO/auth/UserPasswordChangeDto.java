package com.ediary.DTO.auth;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPasswordChangeDto {

    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;
}
