package com.sparta.filmfly.domain.movie.controller;

import com.sparta.filmfly.domain.movie.dto.ApiDiscoverMovieRequestDto;
import com.sparta.filmfly.domain.movie.dto.ApiMovieResponseDto;
import com.sparta.filmfly.domain.movie.dto.ApiSearchMovieRequestDto;
import com.sparta.filmfly.domain.movie.dto.GenresResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieDetailSimpleResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieReactionsResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieSearchCond;
import com.sparta.filmfly.domain.movie.dto.MovieSimpleResponseDto;
import com.sparta.filmfly.domain.movie.entity.OriginLanguageEnum;
import com.sparta.filmfly.domain.movie.service.MovieService;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
import com.sparta.filmfly.global.common.response.CursorResponseDto;
import com.sparta.filmfly.global.common.response.DataResponseDto;
import com.sparta.filmfly.global.common.response.MessageResponseDto;
import com.sparta.filmfly.global.util.PageUtils;
import com.sparta.filmfly.global.util.ResponseUtils;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        List<ApiMovieResponseDto> responseDto = movieService.apiRequestForMovie(apiSearchMovieRequestDto);
        return ResponseUtils.success(responseDto);
    }

    /**
     * 장르 api 가져오기
     */
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @GetMapping("/genres/api")
    public ResponseEntity<MessageResponseDto> apiGenresRequest() {
        movieService.apiGenresRequest(OriginLanguageEnum.EN);
        movieService.apiGenresRequest(OriginLanguageEnum.KO);
        return ResponseUtils.success();
    }

    /**
     * 장르 가져오기
     */
    @GetMapping("/genres")
    public ResponseEntity<DataResponseDto<List<GenresResponseDto>>> getGenres() {
        List<GenresResponseDto> genresResponseDtoList = movieService.getGenres();
        return ResponseUtils.success(genresResponseDtoList);
    }

    /**
    * 영화 검색 (페이징)
    */
    @GetMapping("/movies")
    public ResponseEntity<DataResponseDto<CursorResponseDto<MovieReactionsResponseDto>>> getListMovie(
        @RequestParam(required = false) Double lastPopularity,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "25") int size,
        @RequestParam(required = false, defaultValue = "id") String sortBy,
        @RequestParam(required = false, defaultValue = "true") boolean isAsc,
        @RequestParam(required = false, defaultValue = "") String search,
        @RequestParam(required = false) List<Integer> genreIds,
        @RequestParam(required = false) List<Integer> adults,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate releaseDateFrom,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate releaseDateTo
    ) {
        MovieSearchCond movieSearchCond = MovieSearchCond.createSearchCondition(
            search, genreIds, adults, releaseDateFrom, releaseDateTo
        );

        Pageable pageable = PageUtils.of(page, size, sortBy, isAsc);

        CursorResponseDto<MovieReactionsResponseDto> responseDto = movieService.getPageMovieBySearchCond(
            movieSearchCond, lastPopularity, pageable
        );
        return ResponseUtils.success(responseDto);
    }

    /**
     * 최신 인기 영화
     */
    @GetMapping("/movies/trend")
    public ResponseEntity<DataResponseDto<List<MovieSimpleResponseDto>>> getMovieList() {
        List<MovieSimpleResponseDto> responseDto = movieService.getMovieTrendList();
        return ResponseUtils.success(responseDto);
    }

    /**
     * 영화 상세(단건) 조회
     */
    @GetMapping("/movies/{movieId}")
    public ResponseEntity<DataResponseDto<MovieDetailSimpleResponseDto>> getMovie(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long movieId
    ) {
        MovieDetailSimpleResponseDto responseDto = movieService.getMovie(userDetails, movieId);
        return ResponseUtils.success(responseDto);
    }
}