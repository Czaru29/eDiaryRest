package com.ediary.DTO;

import lombok.*;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentDto {

    private Long id;

    //User
    private Long userId;
    private String userName;

}
