package com.ediary.DTO;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndYearReportDtoShort {

    private Long id;
    private String year;
    private Long userId;
    private String name;
}
