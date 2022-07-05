package com.ediary.DTO;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndYearReportDto {

    private Long id;

    private String year;

    //Teacher
    private Long teacherId;
    private String teacherName;

    //Student
    private Long studentId;
    private String studentName;
}
