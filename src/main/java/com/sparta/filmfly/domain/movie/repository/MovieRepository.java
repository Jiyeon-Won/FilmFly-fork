package com.sparta.filmfly.domain.movie.repository;

import com.sparta.filmfly.domain.movie.entity.Movie;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.detail.NotFoundException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long>, MovieRepositoryCustom {

    List<Movie> findTop20ByOrderByPopularityDesc();

    default Movie findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(
                () -> new NotFoundException(ResponseCodeEnum.MOVIE_NOT_FOUND)
        );
    }

    long count();

    default void existsByIdOrElseThrow(Long movieId) {
        if (!existsById(movieId)) {
            throw new NotFoundException(ResponseCodeEnum.MOVIE_NOT_FOUND);
        }
    }
}