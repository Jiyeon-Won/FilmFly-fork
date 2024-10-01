package com.sparta.filmfly.domain.favorite.repository;

import com.sparta.filmfly.domain.movie.dto.MovieResponseDto;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import org.springframework.data.domain.Pageable;

public interface FavoriteRepositoryCustom {

    PageResponseDto<MovieResponseDto> getPageFavorite(Long userId, Pageable pageable);
}