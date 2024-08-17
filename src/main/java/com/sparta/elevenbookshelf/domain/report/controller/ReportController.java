package com.sparta.elevenbookshelf.domain.report.controller;

import com.sparta.elevenbookshelf.domain.report.dto.ReportMapResponseDto;
import com.sparta.elevenbookshelf.domain.report.dto.ReportRequestDto;
import com.sparta.elevenbookshelf.domain.report.dto.ReportResponseDto;
import com.sparta.elevenbookshelf.domain.report.service.ReportService;
import com.sparta.elevenbookshelf.domain.user.dto.UserResponseDto;
import com.sparta.elevenbookshelf.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
@Slf4j(topic = "UserController")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<?> createReport(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody ReportRequestDto reportRequestDto,
            @RequestParam Long postId,
            @RequestParam Long commentId) {
        reportService.createReport(user.getUser().getId(),reportRequestDto,postId,commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ReportMapResponseDto> getReports(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam boolean asc) {
        return ResponseEntity.status(HttpStatus.OK).body(reportService.getReports(page, size, sortBy, asc));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{reportId}")
    public ResponseEntity<?> getReport(
            @PathVariable Long reportId) {
        return ResponseEntity.status(HttpStatus.OK).body(reportService.getReport(reportId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{reportId}")
    public ResponseEntity<?> putReport(
            @PathVariable Long reportId) {
        reportService.putReport(reportId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{reportId}")
    public ResponseEntity<?> deleteReport(
            @PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



}
