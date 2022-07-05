package com.ediary.DTO;

import lombok.*;

import java.util.Date;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentCardDto {

    private Long id;
    private Date date;
    private String content;

    private Long studentId;
    private String studentName;

    private Long teacherId;
    private String teacherName;

}
