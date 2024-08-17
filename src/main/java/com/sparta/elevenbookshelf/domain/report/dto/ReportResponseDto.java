package com.sparta.elevenbookshelf.domain.report.dto;

import com.sparta.elevenbookshelf.domain.report.entity.Report;
import lombok.Data;

@Data
public class ReportResponseDto {

    private Long reportId;
    private String title;
    private String content;
    private Long postId;
    private Long commentId;
    private Report.ReportStatus reportStatus;


    public ReportResponseDto(Report report) {
        this.reportId = report.getId();
        this.title = report.getTitle();
        this.content = report.getContent();
        this.reportStatus = report.getReportStatus();

        if (report.getPost() != null) {
            this.postId = report.getPost().getId();
        }

        if (report.getComment() != null) {
            this.commentId = report.getComment().getId();
        }
    }

}
