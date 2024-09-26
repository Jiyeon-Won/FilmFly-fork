package com.sparta.filmfly.domain.movie.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.filmfly.domain.favorite.entity.QFavorite;
import com.sparta.filmfly.domain.movie.dto.CreditSimpleResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieDetailSimpleResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieReactionCheckResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieReactionsResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieSearchCond;
import com.sparta.filmfly.domain.movie.dto.MovieSimpleResponseDto;
import com.sparta.filmfly.domain.movie.entity.Movie;
import com.sparta.filmfly.domain.movie.entity.QCredit;
import com.sparta.filmfly.domain.movie.entity.QMovie;
import com.sparta.filmfly.domain.movie.entity.QMovieCredit;
import com.sparta.filmfly.domain.reaction.ReactionContentTypeEnum;
import com.sparta.filmfly.domain.reaction.entity.QBad;
import com.sparta.filmfly.domain.reaction.entity.QGood;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MovieRepositoryImpl implements MovieRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Movie> findByGenreIds(List<Integer> genreIds) {
        QMovie movie = QMovie.movie;

        BooleanExpression predicate = null;
        for (Integer genreId : genreIds) {
            BooleanExpression expression = movie.genreIds.any().eq(genreId);
            predicate = (predicate == null) ? expression : predicate.and(expression);
        }

        return queryFactory.selectFrom(movie)
                .where(predicate)
                .fetch();
    }

    /**
     * 영화 조회 (페이징)
     */
    @Override
    public PageResponseDto<MovieReactionsResponseDto> getPageMovieBySearchCond(
        MovieSearchCond searchOptions, Pageable pageable
    ) {
        QMovie qMovie = QMovie.movie;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        List<MovieReactionsResponseDto> fetch = queryFactory
            .select(Projections.constructor(MovieReactionsResponseDto.class,
                qMovie.id,
                qMovie.title,
                qMovie.originalTitle,
                qMovie.posterPath,
                qMovie.backdropPath,
                qGood.id.countDistinct().as("goodCount"),
                qBad.id.countDistinct().as("badCount")
            ))
            .from(qMovie)
            .leftJoin(qGood).on(qGood.typeId.eq(qMovie.id)
                .and(qGood.type.eq(ReactionContentTypeEnum.MOVIE))
            )
            .leftJoin(qBad).on(qBad.typeId.eq(qMovie.id)
                .and(qBad.type.eq(ReactionContentTypeEnum.MOVIE))
            )
            .where(searchPredicate(searchOptions))
            .groupBy(qMovie.id)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(qMovie.count())
            .from(qMovie)
            .where(searchPredicate(searchOptions))
            .fetchOne();

        PageImpl<MovieReactionsResponseDto> page = new PageImpl<>(fetch, pageable, total);

        return PageResponseDto.of(page);
    }

    /**
     * 영화 단일 조회
     */
    @Override
    public MovieDetailSimpleResponseDto getMovie(Long movieId) {
        QMovie qMovie = QMovie.movie;
        QCredit qCredit = QCredit.credit;
        QMovieCredit qMovieCredit = QMovieCredit.movieCredit;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        List<Tuple> tuples = queryFactory.select(
                Projections.constructor(MovieSimpleResponseDto.class,
                    qMovie.id,
                    qMovie.title,
                    qMovie.originalTitle,
                    qMovie.posterPath,
                    qMovie.backdropPath,
                    qMovie.overview
                ),
                Projections.constructor(CreditSimpleResponseDto.class,
                    qCredit.id,
                    qCredit.name,
                    qCredit.originalName,
                    qCredit.profilePath
                ),
                qGood.id.countDistinct().as("goodCount"),
                qBad.id.countDistinct().as("badCount")
            )
            .from(qMovieCredit)
            .join(qMovie).on(qMovieCredit.movie.id.eq(qMovie.id))
            .join(qCredit).on(qMovieCredit.credit.id.eq(qCredit.id))
            .leftJoin(qGood).on(qGood.typeId.eq(qMovie.id)
                .and(qGood.type.eq(ReactionContentTypeEnum.MOVIE))
            )
            .leftJoin(qBad).on(qBad.typeId.eq(qMovie.id)
                .and(qBad.type.eq(ReactionContentTypeEnum.MOVIE))
            )
            .where(qMovie.id.eq(movieId))
            .groupBy(qMovie.id, qCredit.id)
            .fetch();

        MovieSimpleResponseDto movieDto = null;
        List<CreditSimpleResponseDto> creditDtos = null;
        Long goodCount = null;
        Long badCount = null;

        if (!tuples.isEmpty()) {
            movieDto = tuples.get(0).get(0, MovieSimpleResponseDto.class);
            creditDtos = tuples.stream()
                .map(tuple -> tuple.get(1, CreditSimpleResponseDto.class))
                .toList();
            goodCount = tuples.get(0).get(2, Long.class);
            badCount = tuples.get(0).get(3, Long.class);
        }

        return MovieDetailSimpleResponseDto.builder()
            .movie(movieDto)
            .credits(creditDtos)
            .goodCount(goodCount)
            .badCount(badCount)
            .build();
    }

    /**
     * 영화 단일 조회 시 해당 사용자가 영화를 좋아요, 싫어요, 찜하기를 눌렀는지 확인하는 코드
     */
    @Override
    public MovieReactionCheckResponseDto checkMovieReaction(User user, Long movieId) {
        QMovie qMovie = QMovie.movie;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;
        QFavorite qFavorite = QFavorite.favorite;

        MovieReactionCheckResponseDto fetchOne = queryFactory.select(
                Projections.constructor(
                    MovieReactionCheckResponseDto.class,
                    qGood.id.isNotNull(),
                    qBad.id.isNotNull(),
                    qFavorite.id.isNotNull()
                ))
            .from(qMovie)
            .leftJoin(qGood).on(
                qGood.user.eq(user)
                    .and(qMovie.id.eq(qGood.typeId))
                    .and(qGood.type.eq(ReactionContentTypeEnum.MOVIE))
            )
            .leftJoin(qBad).on(
                qBad.user.eq(user)
                    .and(qMovie.id.eq(qBad.typeId))
                    .and(qBad.type.eq(ReactionContentTypeEnum.MOVIE))
            )
            .leftJoin(qFavorite).on(
                qFavorite.user.eq(user)
                    .and(qMovie.id.eq(qFavorite.movie.id))
            )
            .where(qMovie.id.eq(movieId))
            .fetchOne();

        return fetchOne;
    }

    private BooleanExpression searchPredicate(MovieSearchCond searchOptions) {
        QMovie qMovie = QMovie.movie;
        BooleanExpression predicate = qMovie.isNotNull();

        predicate = predicate.and(searchKeyword(searchOptions.getKeyword()));
        predicate = predicate.and(searchGenres(searchOptions.getGenreIds()));
        predicate = predicate.and(searchAdults(searchOptions.getAdults()));
        predicate = predicate.and(searchDateRange(searchOptions.getReleaseDateFrom(), searchOptions.getReleaseDateTo()));

        return predicate;
    }

    private BooleanExpression searchKeyword(String keyword) {
        QMovie qMovie = QMovie.movie;
        if (StringUtils.hasText(keyword)) {
            return qMovie.title.like("%" + keyword + "%");
        }
        return null;
    }

    private BooleanExpression searchGenres(List<Integer> genreIds) {
        QMovie qMovie = QMovie.movie;
        BooleanExpression predicate = null;
        if (!genreIds.isEmpty()) {
            for (Integer genreId : genreIds) {
                BooleanExpression expression = qMovie.genreIds.any().eq(genreId);
                // 현재는 genreIds에 포함되어 있는 모든 id값이 일치해야 출력을 함
                // 만약 하나라도 포함이 되어있는 영화를 출력하고 싶은면 or로 변경
                predicate = (predicate == null) ? expression : predicate.and(expression);
            }
        }
        return predicate;
    }

    private BooleanExpression searchAdults(List<Integer> adults) {
        QMovie qMovie = QMovie.movie;
        BooleanExpression predicate = null;
        if (!adults.isEmpty()) {
            for (Integer adult : adults) {
                BooleanExpression expression = qMovie.adult.eq(adult == 1);
                predicate = (predicate == null) ? expression : predicate.or(expression);
            }
        }
        return predicate;
    }

    private BooleanExpression searchDateRange(LocalDate releaseDateFrom, LocalDate releaseDateTo) {
        QMovie qMovie = QMovie.movie;
        BooleanExpression predicate = null;

        if (releaseDateFrom != null && releaseDateTo != null) {
            predicate = qMovie.releaseDate.between(releaseDateFrom.toString(), releaseDateTo.toString());
        } else if (releaseDateFrom != null) {
            predicate = qMovie.releaseDate.goe(releaseDateFrom.toString());
        } else if (releaseDateTo != null) {
            predicate = qMovie.releaseDate.loe(releaseDateTo.toString());
        }

        return predicate;
    }
}