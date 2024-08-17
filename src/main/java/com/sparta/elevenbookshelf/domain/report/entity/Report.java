package com.sparta.elevenbookshelf.domain.report.entity;

import com.sparta.elevenbookshelf.common.Timestamp;
import com.sparta.elevenbookshelf.domain.comment.entity.Comment;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Report extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    private String title;

    private String content;

    private ReportStatus reportStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "psot_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Builder
    public Report(String title, String content, User user, Post post, Comment comment,ReportStatus reportStatus) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.post = post;
        this.comment = comment;
        this.reportStatus = reportStatus;
    }

    @Getter
    @RequiredArgsConstructor
    public enum ReportStatus {
        UNVERIFIED,  // 미확인
        VERIFIED,    // 확인
        COMPLETED,   // 완료됨
        PENDING      // 보류됨
    }

    public void updateReportStatus(ReportStatus reportStatus){
        this.reportStatus = reportStatus;
    }

}
