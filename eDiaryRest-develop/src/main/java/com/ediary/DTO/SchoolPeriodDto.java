package com.ediary.DTO;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchoolPeriodDto {

    private Long id;
    private String description;

    private String dayName;
    private Long dayId;

    private int durationNumber;
    private Long durationId;
    private String startTime;
    private String endTime;

    private String subjectName;
    private Long subjectId;

    private String teacherName;
    private Long teacherId;

    private String classroomName;
    private Long classroomId;

    private String schoolClassName;
    private Long schoolClassId;
}
