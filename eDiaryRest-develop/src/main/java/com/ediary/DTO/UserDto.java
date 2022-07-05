package com.ediary.DTO;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 255)
    private String Name;

    private Long messageNumber;

    //Data for admin
    @NotBlank
    @Size(min = 2, max = 25)
    private String username;

    @NotBlank
    @Size(min = 2, max = 25)
    private String password;

    private Boolean isEnabled;

    //Address
    @Valid
    private AddressDto address;

    //Roles
    private List<RoleDto> roles;

}
