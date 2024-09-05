package com.sparta.filmfly.domain.user.controller;

import com.sparta.filmfly.domain.board.service.BoardService;
import com.sparta.filmfly.domain.movie.service.MovieService;
import com.sparta.filmfly.domain.report.dto.ReportPageResponseDto;
import com.sparta.filmfly.domain.report.dto.ReportResponseDto;
import com.sparta.filmfly.domain.report.service.ReportService;
import com.sparta.filmfly.domain.user.dto.UserResponseDto;
import com.sparta.filmfly.domain.user.dto.UserSearchPageResponseDto;
import com.sparta.filmfly.domain.user.dto.UserStateChangeRequestDto;
import com.sparta.filmfly.domain.user.entity.UserStatusEnum;
import com.sparta.filmfly.domain.user.service.UserService;
import com.sparta.filmfly.global.common.response.DataResponseDto;
import com.sparta.filmfly.global.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ReportService reportService;
    private final BoardService boardService;
    private final MovieService movieService;

    /**
     * 유저 검색 조회
     */
    @GetMapping("/users")
    public ResponseEntity<DataResponseDto<UserSearchPageResponseDto>> getUsersBySearch(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserStatusEnum status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        UserSearchPageResponseDto users = userService.getUsersBySearch(search, status, page-1, size);
        return ResponseUtils.success(users);
    }

    /**
     * 개인 유저 상세 조회
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<DataResponseDto<UserResponseDto>> getUserDetail(
            @PathVariable Long userId
    ) {
        UserResponseDto userDetail = userService.getUserDetail(userId);
        return ResponseUtils.success(userDetail);
    }

    /**
     * 신고 목록 조회
     */
    @GetMapping("/reports")
    public ResponseEntity<DataResponseDto<ReportPageResponseDto>> getAllReports(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order
    ) {
        ReportPageResponseDto reports = reportService.getAllReports(page - 1, size, sortBy, order);
        return ResponseUtils.success(reports);
    }


    /**
     * 신고 상세 조회
     */
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<DataResponseDto<ReportResponseDto>> getReportDetail(
            @PathVariable Long reportId
    ) {
        ReportResponseDto reportDetail = reportService.getReportDetail(reportId);
        return ResponseUtils.success(reportDetail);
    }

    /**
     * 유저 정지
     */
    @PatchMapping("/suspend")
    public ResponseEntity<DataResponseDto<UserResponseDto>> suspendUser(
            @RequestBody UserStateChangeRequestDto userStateChangeRequestDto
    ) {
        UserResponseDto userResponseDto = userService.suspendUser(userStateChangeRequestDto.getUserId());
        return ResponseUtils.success(userResponseDto);
    }


    /**
     * 유저 활성화 시키기
     */
    @PatchMapping("/activate")
    public ResponseEntity<DataResponseDto<UserResponseDto>> activateUser(
            @RequestBody UserStateChangeRequestDto userStateChangeRequestDto
    ) {
        UserResponseDto userResponseDto = userService.activateUserAsAdmin(userStateChangeRequestDto.getUserId());
        return ResponseUtils.success(userResponseDto);
    }

    /**
     * 주요 데이터 통계 정보 조회
     */
    @GetMapping("/statistics")
    public ResponseEntity<DataResponseDto<Map<String, Long>>> getStats() {
        long userCount = userService.getUserCount();
        long postCount = boardService.getBoardCount();
        long movieCount = movieService.getMovieCount();

        Map<String, Long> stats = new HashMap<>();
        stats.put("userCount", userCount);
        stats.put("boardCount", postCount);
        stats.put("movieCount", movieCount);

        return ResponseUtils.success(stats);
    }

}