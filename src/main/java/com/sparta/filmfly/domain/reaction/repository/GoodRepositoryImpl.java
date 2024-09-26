package com.sparta.filmfly.domain.reaction.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.filmfly.domain.board.entity.QBoard;
import com.sparta.filmfly.domain.comment.entity.QComment;
import com.sparta.filmfly.domain.movie.entity.QMovie;
import com.sparta.filmfly.domain.reaction.ReactionContentTypeEnum;
import com.sparta.filmfly.domain.reaction.dto.ReactionBoardResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionCommentResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionMovieResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionReviewResponseDto;
import com.sparta.filmfly.domain.reaction.entity.QBad;
import com.sparta.filmfly.domain.reaction.entity.QGood;
import com.sparta.filmfly.domain.review.entity.QReview;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GoodRepositoryImpl implements GoodRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 사용자가 좋아요를 누른 영화 조회 (페이징)
     */
    @Override
    public PageResponseDto<ReactionMovieResponseDto> getPageMovieByUserGood(
        Long userId, Pageable pageable
    ) {
        QMovie qMovie = QMovie.movie;
        QGood qGood = QGood.good;

        List<ReactionMovieResponseDto> fetch = queryFactory
            .select(Projections.constructor(ReactionMovieResponseDto.class,
                qGood.id,
                qMovie.id.as("movieId"),
                qMovie.title,
                qMovie.originalTitle,
                qMovie.posterPath,
                qMovie.backdropPath
            ))
            .from(qGood)
            .join(qMovie).on(qGood.typeId.eq(qMovie.id)
                .and(qGood.user.id.eq(userId))
            )
            .where(qGood.type.eq(ReactionContentTypeEnum.MOVIE))
            .orderBy(qGood.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(qMovie.count())
            .from(qMovie)
            .innerJoin(qGood).on(qGood.type.eq(ReactionContentTypeEnum.MOVIE)
                .and(qGood.typeId.eq(qMovie.id))
                .and(qGood.user.id.eq(userId)))
            .fetchOne();

        PageImpl<ReactionMovieResponseDto> page = new PageImpl<>(fetch, pageable, total);

        return PageResponseDto.of(page);
    }

    /**
     * 사용자가 좋아요를 누른 리뷰 조회 (페이징)
     */
    @Override
    public PageResponseDto<ReactionReviewResponseDto> getPageReviewByUserGood(
        Long userId, Pageable pageable
    ) {
        QMovie qMovie = QMovie.movie;
        QReview qReview = QReview.review;
        QGood qGood = QGood.good;

        List<ReactionReviewResponseDto> fetch = queryFactory.select(Projections.constructor(
                ReactionReviewResponseDto.class,
                qGood.id,
                qReview.id,
                qReview.user.id,
                qReview.movie.id,
                qReview.movie.title,
                qReview.user.nickname,
                qReview.user.pictureUrl,
                qReview.rating,
                qReview.title,
                qReview.content,
                qReview.createdAt
            ))
            .from(qGood)
            .join(qReview).on(
                qGood.typeId.eq(qReview.id)
                    .and(qGood.user.id.eq(userId))
            )
            .join(qReview).on(qReview.movie.eq(qMovie))
            .where(qGood.type.eq(ReactionContentTypeEnum.REVIEW))
            .orderBy(qGood.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(qReview.count())
            .from(qReview)
            .innerJoin(qGood).on(qGood.type.eq(ReactionContentTypeEnum.REVIEW)
                .and(qGood.typeId.eq(qReview.id))
                .and(qGood.user.id.eq(userId)))
            .fetchOne();

        PageImpl<ReactionReviewResponseDto> page = new PageImpl<>(fetch, pageable, total);

        return PageResponseDto.of(page);
    }

    /**
     * 사용자가 좋아요를 누른 게시물 조회 (페이징)
     */
    @Override
    public PageResponseDto<ReactionBoardResponseDto> getPageBoardByUserGood(
        Long userId, Pageable pageable
    ) {
        QBoard qBoard = QBoard.board;
        QGood qGood = QGood.good;

        List<ReactionBoardResponseDto> fetch = queryFactory
            .select(Projections.constructor(ReactionBoardResponseDto.class,
                qGood.id,
                qBoard.id,
                qBoard.user.id,
                qBoard.title,
                qBoard.user.nickname,
                qBoard.createdAt,
                qBoard.hits
            ))
            .from(qGood)
            .join(qBoard).on(
                qGood.typeId.eq(qBoard.id)
                    .and(qGood.user.id.eq(userId))
            )
            .where(qGood.type.eq(ReactionContentTypeEnum.BOARD))
            .orderBy(qGood.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(qBoard.count())
            .from(qBoard)
            .join(qGood).on(qGood.type.eq(ReactionContentTypeEnum.BOARD)
                .and(qGood.typeId.eq(qBoard.id))
                .and(qGood.user.id.eq(userId)))
            .fetchOne();

        PageImpl<ReactionBoardResponseDto> page = new PageImpl<>(fetch, pageable, total);

        return PageResponseDto.of(page);
    }

    /**
     * 사용자가 좋아요를 누른 댓글 조회 (페이징)
     */
    @Override
    public PageResponseDto<ReactionCommentResponseDto> getPageCommentByUserGood(
        Long userId, Pageable pageable
    ) {
        QBoard qBoard = QBoard.board;
        QComment qComment = QComment.comment;
        QGood qGood = QGood.good;

        List<ReactionCommentResponseDto> fetch = queryFactory
            .select(Projections.constructor(ReactionCommentResponseDto.class,
                qGood.id,
                qComment.id,
                qComment.user.id,
                qComment.board.id,
                qComment.user.nickname,
                qComment.content,
                qComment.createdAt
            ))
            .from(qGood)
            .join(qComment).on(
                qGood.typeId.eq(qComment.id)
                    .and(qGood.user.id.eq(userId))
            )
            .join(qComment).on(qComment.board.eq(qBoard))
            .where(qGood.type.eq(ReactionContentTypeEnum.COMMENT))
            .orderBy(qGood.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(qComment.count())
            .from(qComment)
            .innerJoin(qGood).on(qGood.type.eq(ReactionContentTypeEnum.COMMENT)
                .and(qGood.typeId.eq(qComment.id))
                .and(qGood.user.id.eq(userId)))
            .fetchOne();

        PageImpl<ReactionCommentResponseDto> page = new PageImpl<>(fetch, pageable, total);

        return PageResponseDto.of(page);
    }
}