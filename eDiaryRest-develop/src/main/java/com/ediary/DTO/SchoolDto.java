package com.ediary.DTO;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchoolDto {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 255)
    private String name;

    @NotBlank
    @Size(min = 2, max = 255)
    private String headmasterName;

    @NotBlank
    @Size(min = 2, max = 255)
    private String headmasterLastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String schoolOffice;

    //Address
    @Valid
    private AddressDto addressDto;
}
