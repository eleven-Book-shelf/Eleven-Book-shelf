package com.sparta.elevenbookshelf.domain.report.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.elevenbookshelf.domain.post.entity.Post;
import com.sparta.elevenbookshelf.domain.report.entity.Report;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.sparta.elevenbookshelf.domain.report.entity.QReport.report;


@Repository
@RequiredArgsConstructor
public class ReportRepositoryCustomImpl implements ReportRepositoryCustom  {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Report> findByReports(Report.ReportStatus type, int page, int size, boolean asc) {
        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(
                asc ? Order.ASC : Order.DESC,
                report.createdAt
        );

        long totalCount = Optional.ofNullable(
                jpaQueryFactory
                        .select(report.count())
                        .from(report)
                        .where(report.reportStatus.eq(type))
                        .fetchFirst()
        ).orElse(0L);

        Pageable pageable = PageRequest.of(page, size);

        List<Report> reportList = jpaQueryFactory.selectFrom(report)
                .where(report.reportStatus.eq(type))
                .offset(pageable.getOffset())
                .limit(size)
                .orderBy(orderSpecifier)
                .fetch();

        return new PageImpl<>(reportList, pageable, totalCount);
    }
}
