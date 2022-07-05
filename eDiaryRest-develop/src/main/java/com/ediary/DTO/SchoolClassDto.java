package com.ediary.DTO;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolClassDto {

    private Long id;

    @NotNull
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[0-9].*")
    private String name;

    private Long studentCouncilId;

    private String teacherName;
    @NotNull
    private Long teacherId;

    private Long parentCouncilId;

    private List<StudentDto> students;

    private List<Long> eventsId;

    private List<Long> schoolPeriodsId;

    private List<Long> lessonsId;
}
