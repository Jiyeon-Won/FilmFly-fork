package com.sparta.filmfly.domain.movie.dto;

import com.sparta.filmfly.domain.movie.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MovieResponseDto {
    private Long id;
    private boolean adult;
    private String backdropPath;
    private String originalLanguage;
    private String originalTitle;
    private String overview;
    private double popularity;
    private String posterPath;
    private String releaseDate;
    private String title;

    public MovieResponseDto(Long id, String backdropPath, String originalTitle, String posterPath, String title) {
        this.id = id;
        this.backdropPath = backdropPath;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.title = title;
    }

    public static MovieResponseDto fromEntity(Movie movie) {
        return MovieResponseDto.builder()
                .id(movie.getId())
                .adult(movie.isAdult())
                .backdropPath(movie.getBackdropPath())
                .originalLanguage(movie.getOriginalLanguage())
                .originalTitle(movie.getOriginalTitle())
                .overview(movie.getOverview())
                .popularity(movie.getPopularity())
                .posterPath(movie.getPosterPath())
                .releaseDate(movie.getReleaseDate())
                .title(movie.getTitle())
                .build();
    }
}