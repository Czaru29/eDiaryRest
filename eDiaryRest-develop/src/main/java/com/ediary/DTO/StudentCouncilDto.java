package com.ediary.DTO;


import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentCouncilDto {

    private Long id;

    //Students
    private List<StudentDto> students;

    //Class
    private String schoolClassName;
    private Long schoolClassId;
}
