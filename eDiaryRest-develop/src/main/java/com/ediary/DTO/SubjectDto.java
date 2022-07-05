package com.ediary.DTO;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectDto {

    private Long id;
    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    private String teacherName;
    @NotNull
    private Long teacherId;

    private String className;
    private Long classId;

    private List<String> lessonsName;
    private List<Long> lessonsId;

    private List<String> topicsName;
    private List<Long> topicsId;

}
