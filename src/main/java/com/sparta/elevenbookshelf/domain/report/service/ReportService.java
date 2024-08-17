package com.sparta.elevenbookshelf.domain.report.service;

import com.sparta.elevenbookshelf.domain.comment.entity.Comment;
import com.sparta.elevenbookshelf.domain.comment.service.CommentService;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import com.sparta.elevenbookshelf.domain.post.service.PostService;
import com.sparta.elevenbookshelf.domain.report.dto.ReportMapResponseDto;
import com.sparta.elevenbookshelf.domain.report.dto.ReportRequestDto;
import com.sparta.elevenbookshelf.domain.report.dto.ReportResponseDto;
import com.sparta.elevenbookshelf.domain.report.entity.Report;
import com.sparta.elevenbookshelf.domain.report.repository.ReportRepository;
import com.sparta.elevenbookshelf.domain.user.entity.User;
import com.sparta.elevenbookshelf.domain.user.service.UserService;
import com.sparta.elevenbookshelf.exception.BusinessException;
import com.sparta.elevenbookshelf.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final CommentService commentService;
    private final UserService userService;
    private final PostService postService;

    public void createReport(Long userId, ReportRequestDto reportRequestDto, Long postId, Long commentId) {
        User user = userService.getUser(userId);

        Comment comment = (commentId != null && !commentId.toString().isEmpty()) ? commentService.getComment(commentId) : null;
        Post post = (postId != null && !postId.toString().isEmpty()) ? postService.getPost(postId) : null;

        Report.ReportBuilder reportBuilder = Report.builder()
                .user(user)
                .title(reportRequestDto.getTitle())
                .content(reportRequestDto.getContent())
                .reportStatus(Report.ReportStatus.UNVERIFIED);

        if (comment != null) {
            reportBuilder.comment(comment);
        }

        if (post != null) {
            reportBuilder.post(post);
        }

        reportRepository.save(reportBuilder.build());
    }


    public ReportMapResponseDto getReports(int page, int size, String sortBy, boolean asc) {

        Report.ReportStatus type = Report.ReportStatus.valueOf(sortBy);

        Page<Report> reports = reportRepository.findByReports(type, page, size, asc);

        return new ReportMapResponseDto(reports.getTotalPages(), reports.getContent().stream()
                .map(ReportResponseDto::new).toList());
    }

    @Transactional
    public ReportResponseDto getReport(Long reportId) {
        Report report = getReportById(reportId);

        if (report.getReportStatus() == Report.ReportStatus.UNVERIFIED) {
            report.updateReportStatus(Report.ReportStatus.VERIFIED);
        }

        return new ReportResponseDto(report);
    }

    @Transactional
    public void putReport(Long reportId) {
        Report report = getReportById(reportId);
        report.updateReportStatus(Report.ReportStatus.PENDING);
    }

    @Transactional
    public void deleteReport(Long reportId) {
        Report report = getReportById(reportId);
        report.updateReportStatus(Report.ReportStatus.COMPLETED);
    }

    //::::::::::::::::::::::::// TOOL BOX //:::::::::::::::::::::::://

    public Report getReportById(Long reportId){
        return reportRepository.findById(reportId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_REPORT)
        );
    }

    public User getUser(Long userId){
       return userService.getUser(userId);
    }

    public Comment getComment(Long commentId){
        return commentService.getComment(commentId);
    }

    public Post getPost(Long postId){
        return postService.getPost(postId);
    }



}
