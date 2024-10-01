package com.sparta.filmfly.domain.movie.repository;

import com.sparta.filmfly.domain.movie.dto.MovieDetailSimpleResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieReactionCheckResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieReactionsResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieSearchCond;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.common.response.CursorResponseDto;
import org.springframework.data.domain.Pageable;

public interface MovieRepositoryCustom {
    CursorResponseDto<MovieReactionsResponseDto> getPageMovieBySearchCond(MovieSearchCond searchOptions, Double lastPopularity, Pageable pageable);
    MovieDetailSimpleResponseDto getMovie(Long movieId);
    MovieReactionCheckResponseDto checkMovieReaction(User user, Long movieId);
}