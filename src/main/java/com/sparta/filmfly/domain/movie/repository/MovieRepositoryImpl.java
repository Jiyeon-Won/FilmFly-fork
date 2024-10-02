package com.sparta.filmfly.domain.movie.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.filmfly.domain.favorite.entity.QFavorite;
import com.sparta.filmfly.domain.movie.dto.CreditSimpleResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieDetailSimpleResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieReactionCheckResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieReactionsResponseDto;
import com.sparta.filmfly.domain.movie.dto.MovieSearchCond;
import com.sparta.filmfly.domain.movie.dto.MovieSimpleResponseDto;
import com.sparta.filmfly.domain.movie.entity.QCredit;
import com.sparta.filmfly.domain.movie.entity.QGenre;
import com.sparta.filmfly.domain.movie.entity.QMovie;
import com.sparta.filmfly.domain.movie.entity.QMovieCredit;
import com.sparta.filmfly.domain.movie.entity.QMovieGenre;
import com.sparta.filmfly.domain.reaction.ReactionContentTypeEnum;
import com.sparta.filmfly.domain.reaction.entity.QBad;
import com.sparta.filmfly.domain.reaction.entity.QGood;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.common.response.CursorResponseDto;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MovieRepositoryImpl implements MovieRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    /**
     * 영화 조회 (페이징)
     */
    @Override
    public
    CursorResponseDto<MovieReactionsResponseDto> getPageMovieBySearchCond(
        MovieSearchCond searchOptions, Double lastPopularity, Pageable pageable
    ) {
        QMovie qMovie = QMovie.movie;
        QGenre qGenre = QGenre.genre;
        QMovieGenre qMovieGenre = QMovieGenre.movieGenre;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        long start = System.currentTimeMillis();

        List<MovieReactionsResponseDto> fetch = queryFactory
            .select(Projections.constructor(MovieReactionsResponseDto.class,
                qMovie.id,
                qMovie.title,
                qMovie.originalTitle,
                qMovie.posterPath,
                qMovie.backdropPath,
                qMovie.popularity,
                qGood.id.countDistinct().as("goodCount"),
                qBad.id.countDistinct().as("badCount")
            ))
            .from(qMovie)
            .join(qMovieGenre).on(qMovie.id.eq(qMovieGenre.movie.id))
            .join(qGenre).on(qMovieGenre.genre.id.eq(qGenre.id))
            .leftJoin(qGood).on(qGood.typeId.eq(qMovie.id)
                .and(qGood.type.eq(ReactionContentTypeEnum.MOVIE))
            )
            .leftJoin(qBad).on(qBad.typeId.eq(qMovie.id)
                .and(qBad.type.eq(ReactionContentTypeEnum.MOVIE))
            )
            .where(searchPredicate(searchOptions, qMovie, qMovieGenre)
                .and(cursorPredicate(qMovie, lastPopularity)))
            .groupBy(qMovie.popularity, qMovie.id)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(qMovie.popularity.desc(), qMovie.id.asc())
            .fetch();

        long end = System.currentTimeMillis();
        System.out.println("시간 : " + (end - start));

        boolean hasNext = fetch.size() == pageable.getPageSize();

        // 마지막 영화의 id를 가져옴 (없으면 null)
        Long lastFetchedMovieId = fetch.isEmpty()
            ? null
            : fetch.get(fetch.size() - 1).getId();

        return CursorResponseDto.of(fetch, lastFetchedMovieId, hasNext);
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

        MovieReactionCheckResponseDto fetch = queryFactory.select(
                Projections.constructor(
                    MovieReactionCheckResponseDto.class,
                    qGood.id.isNotNull(),
                    qBad.id.isNotNull(),
                    qFavorite.id.isNotNull()
                ))
            .from(qMovie)
            .leftJoin(qFavorite).on(
                qFavorite.user.eq(user)
                    .and(qMovie.id.eq(qFavorite.movie.id))
            )
            .leftJoin(qGood).on(qGood.user.eq(user)
                    .and(qMovie.id.eq(qGood.typeId))
                    .and(qGood.type.eq(ReactionContentTypeEnum.MOVIE))
            )
            .leftJoin(qBad).on(qBad.user.eq(user)
                    .and(qMovie.id.eq(qBad.typeId))
                    .and(qBad.type.eq(ReactionContentTypeEnum.MOVIE))
            )
            .where(qMovie.id.eq(movieId))
            .fetchOne();

        return fetch;
    }

    private BooleanExpression cursorPredicate(QMovie qMovie, Double lastPopularity) {
        if (lastPopularity != null) {
            return qMovie.popularity.loe(lastPopularity);
        }
        return null;
    }

    private BooleanExpression searchPredicate(MovieSearchCond searchOptions, QMovie qMovie, QMovieGenre qMovieGenre) {
        BooleanExpression predicate;

        predicate = searchAdults(searchOptions.getAdults(), qMovie);
        predicate = (predicate == null) ? searchDateRange(searchOptions.getReleaseDateFrom(), searchOptions.getReleaseDateTo(), qMovie) : predicate.and(searchDateRange(searchOptions.getReleaseDateFrom(), searchOptions.getReleaseDateTo(), qMovie));
        predicate = (predicate == null) ? searchGenres(searchOptions.getGenreIds(), qMovieGenre) : predicate.and(searchGenres(searchOptions.getGenreIds(), qMovieGenre));
        predicate = (predicate == null) ? searchKeyword(searchOptions.getKeyword(), qMovie) : predicate.and(searchKeyword(searchOptions.getKeyword(), qMovie));

        return predicate;
    }

    private BooleanExpression searchKeyword(String keyword, QMovie qMovie) {
        if (StringUtils.hasText(keyword)) {
            NumberTemplate<Double> booleanTemplate = Expressions.numberTemplate(Double.class,
                "function('match', {0}, {1})", qMovie.title, keyword + "*");
            return booleanTemplate.gt(0);
        }
        return null;
    }

    private BooleanExpression searchGenres(List<Integer> genreIds, QMovieGenre qMovieGenre) {
        if (!genreIds.isEmpty()) {
            return qMovieGenre.genre.id
                .in(genreIds.stream()
                    .map(Long::valueOf)
                    .toList()
                );
        }
        return null;
    }

    private BooleanExpression searchAdults(List<Integer> adults, QMovie qMovie) {
        if (adults.size() == 1) {
            return qMovie.adult.eq(adults.get(0) == 1);
        }
        return null;
    }

    private BooleanExpression searchDateRange(LocalDate releaseDateFrom, LocalDate releaseDateTo, QMovie qMovie) {
        if (releaseDateFrom != null && releaseDateTo != null) {
            return qMovie.releaseDate.between(releaseDateFrom.toString(), releaseDateTo.toString());
        } else if (releaseDateFrom != null) {
            return qMovie.releaseDate.goe(releaseDateFrom.toString());
        } else if (releaseDateTo != null) {
            return qMovie.releaseDate.loe(releaseDateTo.toString());
        }
        return null;
    }
}