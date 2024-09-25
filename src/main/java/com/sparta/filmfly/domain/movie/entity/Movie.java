package com.sparta.filmfly.domain.movie.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Movie {
    @Id
    private Long id;
    private boolean adult;
    private String backdropPath;
    @ElementCollection
    private List<Integer> genreIds;
    private String originalLanguage;
    private String originalTitle;
    @Column(columnDefinition = "TEXT")
    private String overview;
    private double popularity;
    private String posterPath;
    private String releaseDate;
    private String title;
    private boolean video;
    private double voteAverage;
    private int voteCount;
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovieCredit> movieCreditList;

    // DTO로부터 엔티티를 업데이트하는 메서드
    public void update(
            boolean adult,
            String backdropPath,
            List<Integer> genreIds,
            String originalLanguage,
            String originalTitle,
            String overview,
            double popularity,
            String posterPath,
            String releaseDate,
            String title,
            boolean video,
            double voteAverage,
            int voteCount) {
        this.adult = adult;
        this.backdropPath = backdropPath;
        this.genreIds = genreIds;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.title = title;
        this.video = video;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
    }

}