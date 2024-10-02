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
public class BadRepositoryImpl implements BadRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 사용자가 좋아요를 누른 영화 조회 (페이징)
     */
    @Override
    public PageResponseDto<ReactionMovieResponseDto> getPageMovieByUserBad(
        Long userId, Pageable pageable
    ) {
        QMovie qMovie = QMovie.movie;
        QBad qBad = QBad.bad;

        List<ReactionMovieResponseDto> fetch = queryFactory
            .select(Projections.constructor(ReactionMovieResponseDto.class,
                qBad.id,
                qMovie.id.as("movieId"),
                qMovie.title,
                qMovie.originalTitle,
                qMovie.posterPath,
                qMovie.backdropPath
            ))
            .from(qBad)
            .join(qMovie).on(qBad.typeId.eq(qMovie.id)
                .and(qBad.user.id.eq(userId))
            )
            .where(qBad.type.eq(ReactionContentTypeEnum.MOVIE))
            .orderBy(qBad.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(qMovie.count())
            .from(qMovie)
            .innerJoin(qBad).on(qBad.type.eq(ReactionContentTypeEnum.MOVIE)
                .and(qBad.typeId.eq(qMovie.id))
                .and(qBad.user.id.eq(userId)))
            .fetchOne();

        PageImpl<ReactionMovieResponseDto> page = new PageImpl<>(fetch, pageable, total);

        return PageResponseDto.of(page);
    }

    /**
     * 사용자가 좋아요를 누른 리뷰 조회 (페이징)
     */
    @Override
    public PageResponseDto<ReactionReviewResponseDto> getPageReviewByUserBad(
        Long userId, Pageable pageable
    ) {
        QMovie qMovie = QMovie.movie;
        QReview qReview = QReview.review;
        QBad qBad = QBad.bad;

        List<ReactionReviewResponseDto> fetch = queryFactory.select(Projections.constructor(
                ReactionReviewResponseDto.class,
                qBad.id,
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
            .from(qBad)
            .join(qReview).on(
                qBad.typeId.eq(qReview.id)
                    .and(qBad.user.id.eq(userId))
            )
            .join(qReview).on(qReview.movie.eq(qMovie))
            .where(qBad.type.eq(ReactionContentTypeEnum.REVIEW))
            .orderBy(qBad.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(qReview.count())
            .from(qReview)
            .innerJoin(qBad).on(qBad.type.eq(ReactionContentTypeEnum.REVIEW)
                .and(qBad.typeId.eq(qReview.id))
                .and(qBad.user.id.eq(userId)))
            .fetchOne();

        PageImpl<ReactionReviewResponseDto> page = new PageImpl<>(fetch, pageable, total);

        return PageResponseDto.of(page);
    }

    /**
     * 사용자가 좋아요를 누른 게시물 조회 (페이징)
     */
    @Override
    public PageResponseDto<ReactionBoardResponseDto> getPageBoardByUserBad(
        Long userId, Pageable pageable
    ) {
        QBoard qBoard = QBoard.board;
        QBad qBad = QBad.bad;

        List<ReactionBoardResponseDto> fetch = queryFactory
            .select(Projections.constructor(ReactionBoardResponseDto.class,
                qBad.id,
                qBoard.id,
                qBoard.user.id,
                qBoard.title,
                qBoard.user.nickname,
                qBoard.createdAt,
                qBoard.hits
            ))
            .from(qBad)
            .join(qBoard).on(
                qBad.typeId.eq(qBoard.id)
                    .and(qBad.user.id.eq(userId))
            )
            .where(qBad.type.eq(ReactionContentTypeEnum.BOARD))
            .orderBy(qBad.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(qBoard.count())
            .from(qBoard)
            .join(qBad).on(qBad.type.eq(ReactionContentTypeEnum.BOARD)
                .and(qBad.typeId.eq(qBoard.id))
                .and(qBad.user.id.eq(userId)))
            .fetchOne();

        PageImpl<ReactionBoardResponseDto> page = new PageImpl<>(fetch, pageable, total);

        return PageResponseDto.of(page);
    }

    /**
     * 사용자가 좋아요를 누른 댓글 조회 (페이징)
     */
    @Override
    public PageResponseDto<ReactionCommentResponseDto> getPageCommentByUserBad(
        Long userId, Pageable pageable
    ) {
        QBoard qBoard = QBoard.board;
        QComment qComment = QComment.comment;
        QBad qBad = QBad.bad;

        List<ReactionCommentResponseDto> fetch = queryFactory
            .select(Projections.constructor(ReactionCommentResponseDto.class,
                qBad.id,
                qComment.id,
                qComment.user.id,
                qComment.board.id,
                qComment.user.nickname,
                qComment.content,
                qComment.createdAt
            ))
            .from(qBad)
            .join(qComment).on(
                qBad.typeId.eq(qComment.id)
                    .and(qBad.user.id.eq(userId))
            )
            .join(qComment).on(qComment.board.eq(qBoard))
            .where(qBad.type.eq(ReactionContentTypeEnum.COMMENT))
            .orderBy(qBad.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(qComment.count())
            .from(qComment)
            .innerJoin(qBad).on(qBad.type.eq(ReactionContentTypeEnum.COMMENT)
                .and(qBad.typeId.eq(qComment.id))
                .and(qBad.user.id.eq(userId)))
            .fetchOne();

        PageImpl<ReactionCommentResponseDto> page = new PageImpl<>(fetch, pageable, total);

        return PageResponseDto.of(page);
    }
}