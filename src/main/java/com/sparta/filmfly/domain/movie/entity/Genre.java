package com.sparta.filmfly.domain.movie.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Genre {
    @Id
    private Long id;
    private Long genreId;
    private String name;
}