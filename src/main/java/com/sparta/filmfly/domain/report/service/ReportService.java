package com.sparta.filmfly.domain.report.service;

import com.sparta.filmfly.domain.board.repository.BoardRepository;
import com.sparta.filmfly.domain.comment.repository.CommentRepository;
import com.sparta.filmfly.domain.report.dto.ReportPageResponseDto;
import com.sparta.filmfly.domain.report.dto.ReportRequestDto;
import com.sparta.filmfly.domain.report.dto.ReportResponseDto;
import com.sparta.filmfly.domain.report.entity.Report;
import com.sparta.filmfly.domain.report.entity.ReportTypeEnum;
import com.sparta.filmfly.domain.report.repository.ReportRepository;
import com.sparta.filmfly.domain.review.repository.ReviewRepository;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.domain.user.repository.UserRepository;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.detail.DuplicateException;
import com.sparta.filmfly.global.exception.custom.detail.InvalidTargetException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 유저 신고
     */
    @Transactional
    public void reportUser(Long reporterId, ReportRequestDto reportRequestDto) {
        User reporter = userRepository.findByIdOrElseThrow(reporterId);
        User reported = userRepository.findByIdOrElseThrow(reportRequestDto.getReportedId());

        // 중복 신고 확인
        if (reportRepository.existsByReporterIdAndReportedIdAndTypeIdAndType(reporter, reported, reportRequestDto.getTypeId(), reportRequestDto.getType())) {
            throw new DuplicateException(ResponseCodeEnum.ALREADY_REPORTED);
        }

        // 본인 신고 여부 확인
        if (reporter.equals(reported)) {
            throw new InvalidTargetException(ResponseCodeEnum.INVALID_SELF_TARGET);
        }

        // 원본 내용 가져오기
        String[] contentAndTitle = fetchContent(reportRequestDto.getType(), reportRequestDto.getTypeId());
        String content = "Title: " + contentAndTitle[1] + "\nContent: " + contentAndTitle[0];

        Report report = Report.builder()
                .reporterId(reporter)
                .reportedId(reported)
                .content(content)
                .typeId(reportRequestDto.getTypeId())
                .type(reportRequestDto.getType())
                .reason(reportRequestDto.getReason())
                .build();

        reportRepository.save(report);
    }

    /**
     * 신고 목록 조회 (어드민 권한)
     */
    @Transactional(readOnly = true)
    public ReportPageResponseDto getAllReports(int page, int size, String sortBy, String order) {
        // 정렬 방향 설정 (ASC 또는 DESC)
        Sort.Direction sortDirection = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        // 정렬 정보 포함한 Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        // 페이지 요청에 따른 결과 조회
        Page<Report> reportsPage = reportRepository.findAll(pageable);

        List<ReportResponseDto> reportResponseDtos = reportsPage.getContent().stream()
                .map(report -> {
                    User reporter = userRepository.findByIdOrElseThrow(report.getReporterId().getId());
                    User reported = userRepository.findByIdOrElseThrow(report.getReportedId().getId());
                    return ReportResponseDto.builder()
                            .id(report.getId())
                            .reporterNickname(reporter.getNickname())
                            .reportedNickname(reported.getNickname())
                            .reason(report.getReason())
                            .createdAt(report.getCreatedAt().toString())
                            .build();
                })
                .collect(Collectors.toList());

        return ReportPageResponseDto.builder()
                .reports(reportResponseDtos)
                .totalElements(reportsPage.getTotalElements())
                .totalPages(reportsPage.getTotalPages())
                .currentPage(reportsPage.getNumber() + 1)
                .pageSize(size)
                .build();
    }


    /**
     * 신고 상세 조회
     */
    @Transactional(readOnly = true)
    public ReportResponseDto getReportDetail(Long reportId) {
        Report report = reportRepository.findByIdOrElseThrow(reportId);
        User reporter = userRepository.findByIdOrElseThrow(report.getReporterId().getId());
        User reported = userRepository.findByIdOrElseThrow(report.getReportedId().getId());

        return ReportResponseDto.builder()
                .id(report.getId())
                .reporterId(reporter.getId())
                .reporterNickname(reporter.getNickname())
                .reportedId(reported.getId())
                .reportedNickname(reported.getNickname())
                .content(report.getContent())
                .typeId(report.getTypeId())
                .type(report.getType())
                .reason(report.getReason())
                .createdAt(report.getCreatedAt().toString())
                .build();
    }

    /**
     * 원본 내용 가져오기
     */
    private String[] fetchContent(ReportTypeEnum type, Long typeId) {
        String[] result = new String[2];
        switch (type) {
            case BOARD:
                var board = boardRepository.findByIdOrElseThrow(typeId);
                result[0] = board.getContent();
                result[1] = board.getTitle();
                break;
            case COMMENT:
                var comment = commentRepository.findByIdOrElseThrow(typeId);
                result[0] = comment.getContent();
                result[1] = null;
                break;
            case REVIEW:
                var review = reviewRepository.findByIdOrElseThrow(typeId);
                result[0] = review.getContent();
                result[1] = review.getTitle();
                break;
            default:
                throw new IllegalArgumentException("Unknown report type: " + type);
        }
        return result;
    }
}