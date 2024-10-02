package com.sparta.filmfly.domain.movie.dto;

import com.sparta.filmfly.domain.movie.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MovieReactionsResponseDto {
    private Long id;
    private String title;
    private String originalTitle;
    private String posterPath;
    private String backdropPath;
    private Double popularity;
    private Long goodCount;
    private Long badCount;

    public static MovieReactionsResponseDto fromEntity(Movie movie, Long goodCount, Long badCount) {
        return MovieReactionsResponseDto.builder()
            .id(movie.getId())
            .title(movie.getTitle())
            .originalTitle(movie.getOriginalTitle())
            .posterPath(movie.getPosterPath())
            .backdropPath(movie.getBackdropPath())
            .goodCount(goodCount)
            .badCount(badCount)
            .build();
    }
}