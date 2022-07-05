package com.ediary.domain.helpers;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Report {

    private String filename;
    private byte[] file;
}
