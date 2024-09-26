package com.sparta.filmfly.domain.movie.repository;

import com.sparta.filmfly.domain.movie.dto.MovieCreditResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieDetailResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieDetailSimpleResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieReactionCheckResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieReactionsResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieSearchCond;
import com.sparta.filmfly.domain.movie.entity.Movie;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface MovieRepositoryCustom {
    List<Movie> findByGenreIds(List<Integer> genreIds);
    PageResponseDto<MovieReactionsResponseDto> getPageMovieBySearchCond(MovieSearchCond searchOptions, Pageable pageable);
    MovieDetailSimpleResponseDto getMovie(Long movieId);
    MovieReactionCheckResponseDto checkMovieReaction(User user, Long movieId);
}