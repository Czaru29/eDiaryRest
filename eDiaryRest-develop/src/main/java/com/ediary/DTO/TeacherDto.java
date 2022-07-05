package com.ediary.DTO;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDto {

    private Long userId;
    private Long id;

    private String name;
}
