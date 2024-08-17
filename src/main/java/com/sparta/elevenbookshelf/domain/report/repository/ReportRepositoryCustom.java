package com.sparta.elevenbookshelf.domain.report.repository;

import com.sparta.elevenbookshelf.domain.report.entity.Report;
import org.springframework.data.domain.Page;

public interface ReportRepositoryCustom {

    Page<Report> findByReports(Report.ReportStatus type, int page, int size, boolean asc);

}
