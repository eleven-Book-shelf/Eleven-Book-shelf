package com.sparta.elevenbookshelf.domain.report.dto;

import com.sparta.elevenbookshelf.domain.report.entity.Report;
import lombok.Data;

@Data
public class ReportResponseDto {

    private Long id;
    private String title;
    private String postType;
    private String content;
    private Long postId;
    private Long commentId;
    private Report.ReportStatus reportStatus;

    public ReportResponseDto(Report report) {
        this.id = report.getId();
        this.postType = "Report";
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
