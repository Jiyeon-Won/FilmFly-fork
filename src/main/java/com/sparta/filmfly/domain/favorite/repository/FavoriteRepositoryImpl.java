package com.sparta.filmfly.domain.favorite.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.filmfly.domain.favorite.entity.QFavorite;
import com.sparta.filmfly.domain.movie.dto.MovieResponseDto;
import com.sparta.filmfly.domain.movie.entity.QMovie;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FavoriteRepositoryImpl implements FavoriteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponseDto<MovieResponseDto> getPageFavorite(Long userId, Pageable pageable) {
        QFavorite qFavorite = QFavorite.favorite;
        QMovie qMovie = QMovie.movie;

        List<MovieResponseDto> fetch = queryFactory
            .select(Projections.constructor(MovieResponseDto.class,
                qMovie.id,
                qMovie.backdropPath,
                qMovie.originalTitle,
                qMovie.posterPath,
                qMovie.title
            ))
            .from(qFavorite)
            .join(qMovie).on(qMovie.id.eq(qFavorite.movie.id))
            .where(qFavorite.user.id.eq(userId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(qFavorite.id.desc())
            .fetch();

        JPAQuery<Long> fetchCount = queryFactory
            .select(qFavorite.count())
            .from(qFavorite)
            .where(qFavorite.user.id.eq(userId));

        return PageResponseDto.of(PageableExecutionUtils.getPage(fetch, pageable, fetchCount::fetchOne));
    }
}