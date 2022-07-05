package com.ediary.DTO;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordUpdateDto {

    private String newPassword;
    private String oldPassword;
}
