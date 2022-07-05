package com.ediary.DTO;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndYearReportDtoRequest {

    @Size(min = 1)
    private List<Long> reportIds;
}
