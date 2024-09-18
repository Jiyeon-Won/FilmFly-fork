package com.sparta.filmfly.domain.favorite.service;

import com.sparta.filmfly.domain.favorite.dto.FavoriteResponseDto;
import com.sparta.filmfly.domain.favorite.entity.Favorite;
import com.sparta.filmfly.domain.favorite.repository.FavoriteRepository;
import com.sparta.filmfly.domain.movie.dto.MovieResponseDto;
import com.sparta.filmfly.domain.movie.entity.Movie;
import com.sparta.filmfly.domain.movie.repository.MovieRepository;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MovieRepository movieRepository;

    /**
    * 영화 찜 등록하기
    */
    public FavoriteResponseDto createFavorite(User user, Long movieId) {
        // DB 존재 검증
        Movie movie = movieRepository.findByIdOrElseThrow(movieId);

        favoriteRepository.existsByMovieIdAndUserIdOrElseThrow(movieId, user.getId());

        Favorite favorite = Favorite.builder()
                .movie(movie)
                .user(user)
                .build();
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return FavoriteResponseDto.fromEntity(savedFavorite);
    }

    /**
    * 자신이 찜한 영화 조회
    */
    public PageResponseDto<List<MovieResponseDto>> getPageFavorite(Long userId,Pageable pageable) {
        Page<Favorite> favoriteList = favoriteRepository.findAllByUserId(userId,pageable);
        //movie 하나 하나 선택 마다 select 쿼리 날리주는 중  나중에 수정 필요
        List<Movie> movieList = favoriteList.stream()
            .map(favorite -> movieRepository.findByIdOrElseThrow(favorite.getMovie().getId()))
            .toList();
        return PageResponseDto.<List<MovieResponseDto>>builder()
                .totalElements(favoriteList.getTotalElements())
                .totalPages(favoriteList.getTotalPages())
                .currentPage(favoriteList.getNumber() + 1)
                .pageSize(favoriteList.getSize())
                .data(movieList.stream().map(MovieResponseDto::fromEntity).toList())
                .build();
    }

    /**
    * 찜한 영화 취소하기
    */
    public void deleteFavorite(User user, Long movieId) {
        Favorite favorite = favoriteRepository.findByMovieIdAndUserIdOrElseThrow(movieId, user.getId());
        favoriteRepository.delete(favorite);
    }
}