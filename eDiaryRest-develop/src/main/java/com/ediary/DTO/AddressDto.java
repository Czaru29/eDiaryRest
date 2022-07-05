package com.ediary.DTO;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 255)
    private String street;

    @NotBlank
    @Size(min = 2, max = 255)
    private String city;

    @NotBlank
    @Size(min = 5, max = 255)
    private String state;

    @NotBlank
    @Size(min = 2, max = 255)
    private String zip;

    @NotBlank
    @Size(min = 5, max = 255)
    private String phoneNumber;

    //User
    private Long userId;
}
