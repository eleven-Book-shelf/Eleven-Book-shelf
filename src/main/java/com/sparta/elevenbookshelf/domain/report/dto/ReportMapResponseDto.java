package com.sparta.elevenbookshelf.domain.report.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReportMapResponseDto {

    private int totalPages;
    private List<ReportResponseDto> responseDtoList;

    public ReportMapResponseDto(int totalPages, List<ReportResponseDto> responseDtoList) {
        this.totalPages = totalPages;
        this.responseDtoList = responseDtoList;
    }

}
