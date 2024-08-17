package com.sparta.elevenbookshelf.domain.report.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportRequestDto {

    private String title;
    private String content;

}
