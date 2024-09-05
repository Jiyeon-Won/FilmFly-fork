package com.sparta.filmfly.domain.movie.controller;

import com.sparta.filmfly.domain.movie.dto.*;
import com.sparta.filmfly.domain.movie.entity.Movie;
import com.sparta.filmfly.domain.movie.entity.OriginLanguageEnum;
import com.sparta.filmfly.domain.movie.service.MovieService;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
import com.sparta.filmfly.global.common.response.DataResponseDto;
import com.sparta.filmfly.global.common.response.MessageResponseDto;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import com.sparta.filmfly.global.util.ResponseUtils;
import com.sparta.filmfly.global.util.PageUtils;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    /**
    * API 데이터 크롤링
    */
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PostMapping("/movies/api/discover")
    public ResponseEntity<DataResponseDto<List<ApiMovieResponseDto>>> apiDiscoverMovieRequest(
            @RequestBody ApiDiscoverMovieRequestDto apiDiscoverMovieRequestDto
    ) {
        // 영화 검색
        log.info("In discoverMovieList");
        List<ApiMovieResponseDto> responseDto = movieService.apiRequestForMovie(apiDiscoverMovieRequestDto);
        return ResponseUtils.success(responseDto);
    }

    /**
     * API 데이터 크롤링
     */
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PostMapping("/movies/api/search")
    public ResponseEntity<DataResponseDto<List<ApiMovieResponseDto>>> apiSearchMovieRequest(
            @RequestBody ApiSearchMovieRequestDto apiSearchMovieRequestDto
    ) {
        // 영화 검색
        log.info("In searchMovieList");
        List<ApiMovieResponseDto> responseDto = movieService.apiRequestForMovie(apiSearchMovieRequestDto);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 장르 api 가져오기
     */
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/genres/api")
    public ResponseEntity<MessageResponseDto> apiGenresRequest() {
        log.info("In apiGenresRequest");
        movieService.apiGenresRequest(OriginLanguageEnum.EN);
        movieService.apiGenresRequest(OriginLanguageEnum.KO);
        return ResponseUtils.success();
    }

    /**
     * 장르 가져오기
     */
    @GetMapping("/genres")
    public ResponseEntity<DataResponseDto<List<GenresResponseDto>>> getGenres() {
        log.info("In getGenres");
        List<GenresResponseDto> genresResponseDtoList = movieService.getGenres();
        return ResponseUtils.success(genresResponseDtoList);
    }

    /**
    * 영화 검색 (페이징)
    */
    @GetMapping("/movies")
    public ResponseEntity<DataResponseDto<PageResponseDto<List<MovieReactionsResponseDto>>>> getListMovie(
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "12") int size,
        @RequestParam(required = false, defaultValue = "id") String sortBy,
        @RequestParam(required = false, defaultValue = "true") boolean isAsc,
        @RequestParam(required = false, defaultValue = "") String search,
        @RequestParam(required = false) List<Integer> genreIds,
        @RequestParam(required = false) List<Integer> adults,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate releaseDateFrom,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate releaseDateTo
    ) {
        log.info("In getListMovie");

        MovieSearchCond movieSearchCond = MovieSearchCond.createSearchCondition(
            search, genreIds, adults, releaseDateFrom, releaseDateTo
        );

        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);

        PageResponseDto<List<MovieReactionsResponseDto>> responseDto = movieService.getPageMovieBySearchCond(
            movieSearchCond, pageable
        );
        return ResponseUtils.success(responseDto);
    }

    /**
     * 최신 인기 영화
     */
    @GetMapping("/movies/trend")
    public ResponseEntity<DataResponseDto<PageResponseDto<List<MovieResponseDto>>>> getMovieList(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "true") boolean isAsc
//        @RequestParam
    ) {
            log.info("In getTrendMovie");
            Sort sort = isAsc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page-1, size, sort);
            Page<Movie> moviePage = movieService.getMovieTrendList(pageable);

        return getDataResponseDtoResponseEntity(moviePage);
    }

    @NotNull
    private ResponseEntity<DataResponseDto<PageResponseDto<List<MovieResponseDto>>>> getDataResponseDtoResponseEntity(Page<Movie> moviePage) {
        PageResponseDto<List<MovieResponseDto>> response = PageResponseDto.<List<MovieResponseDto>>builder()
                .data(moviePage.getContent().stream()
                        .map(MovieResponseDto::fromEntity)
                        .collect(Collectors.toList()))
                .totalElements(moviePage.getTotalElements())
                .totalPages(moviePage.getTotalPages())
                .currentPage(moviePage.getNumber() + 1)
                .pageSize(moviePage.getSize())
                .build();
        return ResponseUtils.success(response);
    }

    /**
     * 영화 상세(단건) 조회
     */
    @GetMapping("/movies/{movieId}")
    public ResponseEntity<DataResponseDto<MovieDetailSimpleResponseDto>> getMovie(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long movieId
    ) {
        log.info("In getMovie");
        MovieDetailSimpleResponseDto responseDto = movieService.getMovie(userDetails, movieId);
        return ResponseUtils.success(responseDto);
    }
}