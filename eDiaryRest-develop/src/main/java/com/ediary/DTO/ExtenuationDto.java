package com.ediary.DTO;


import com.ediary.domain.Extenuation;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtenuationDto {

    private Long id;

    @Size(max = 400)
    private String description;
    private Extenuation.Status status;

    private String parentName;
    private Long parentId;

    private List<Long> attendancesId;
    private List<AttendanceDto> attendances;
}
