package com.sparta.filmfly.domain.comment.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.filmfly.domain.comment.dto.CommentResponseDto;
import com.sparta.filmfly.domain.comment.entity.QComment;
import com.sparta.filmfly.domain.reaction.ReactionContentTypeEnum;
import com.sparta.filmfly.domain.reaction.dto.ReactionCheckResponseDto;
import com.sparta.filmfly.domain.reaction.entity.QBad;
import com.sparta.filmfly.domain.reaction.entity.QGood;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import java.sql.SQLOutput;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponseDto<CommentResponseDto> findAllByBoardIdWithReactions(Long boardId, Pageable pageable) {
        QComment comment = QComment.comment;
        QGood good = QGood.good;
        QBad bad = QBad.bad;

        List<CommentResponseDto> fetch = queryFactory
            .select(Projections.constructor(CommentResponseDto.class,
                comment.id,
                comment.user.id,
                comment.board.id,
                comment.user.nickname,
                comment.user.pictureUrl,
                comment.content,
                comment.createdAt,
                good.id.countDistinct().as("goodCount"),
                bad.id.countDistinct().as("badCount")
            ))
            .from(comment)
            .leftJoin(good).on(
                good.type.eq(ReactionContentTypeEnum.COMMENT)
                    .and(good.typeId.eq(comment.id))
            )
            .leftJoin(bad).on(
                bad.type.eq(ReactionContentTypeEnum.COMMENT)
                    .and(bad.typeId.eq(comment.id))
            )
            .where(comment.board.id.eq(boardId))
            .groupBy(comment.id, comment.user.id, comment.user.nickname, comment.content, comment.createdAt)
            .orderBy(comment.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        for (CommentResponseDto commentResponseDto : fetch) {
            System.out.println("댓글시간 : " + commentResponseDto.getId() + " | " + commentResponseDto.getCreatedAt());
        }

        // 페이지의 총 요소 수를 가져옵니다.
        Long total = queryFactory
            .select(comment.count())
            .from(comment)
            .where(comment.board.id.eq(boardId))
            .fetchOne();

        // PageImpl을 사용하여 페이지 정보를 생성합니다.
        PageImpl<CommentResponseDto> page = new PageImpl<>(fetch, pageable, total);

        // PageResponseDto 반환합니다.
        return PageResponseDto.of(page);
    }

    @Override
    public PageResponseDto<CommentResponseDto> findAllByUserId(Long userId, Pageable pageable) {
        QComment comment = QComment.comment;
        QGood good = QGood.good;
        QBad bad = QBad.bad;

        JPQLQuery<CommentResponseDto> query = queryFactory
                .select(Projections.constructor(CommentResponseDto.class,
                        comment.id,
                        comment.user.id,
                        comment.board.id,
                        comment.user.nickname,
                        comment.user.pictureUrl,
                        comment.content,
                        comment.updatedAt,
                        good.id.countDistinct().as("goodCount"),
                        bad.id.countDistinct().as("badCount")
                ))
                .from(comment)
                .leftJoin(good).on(good.type.eq(ReactionContentTypeEnum.COMMENT).and(good.typeId.eq(comment.id)))
                .leftJoin(bad).on(bad.type.eq(ReactionContentTypeEnum.COMMENT).and(bad.typeId.eq(comment.id)))
                .where(comment.user.id.eq(userId))
                .groupBy(comment.id, comment.user.id, comment.user.nickname, comment.content, comment.updatedAt)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(comment.createdAt.desc()); // 기본 정렬 조건: 생성 일자 최신순

        List<CommentResponseDto> content = query.fetch();
        long total = query.fetchCount();  // 페이지의 총 요소 수를 가져옵니다.

        // PageImpl을 사용하여 페이지 정보를 생성합니다.
        PageImpl<CommentResponseDto> page = new PageImpl<>(content, pageable, total);

        // PageResponseDto 반환합니다.
        return PageResponseDto.of(page);
    }

    @Override
    public List<ReactionCheckResponseDto> checkCommentReaction(User user, List<Long> commentIds) {
        QComment qComment = QComment.comment;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        List<ReactionCheckResponseDto> fetch = queryFactory
            .select(
                Projections.constructor(
                    ReactionCheckResponseDto.class,
                    qGood.id.isNotNull(),
                    qBad.id.isNotNull()
                )
            )
            .from(qComment)
            .leftJoin(qGood).on(
                qGood.user.eq(user)
                    .and(qComment.id.eq(qGood.typeId))
                    .and(qGood.type.eq(ReactionContentTypeEnum.COMMENT))
            )
            .leftJoin(qBad).on(
                qBad.user.eq(user)
                    .and(qComment.id.eq(qBad.typeId))
                    .and(qBad.type.eq(ReactionContentTypeEnum.COMMENT))
            )
            .where(qComment.id.in(commentIds))
            .orderBy(qComment.createdAt.desc())
            .fetch();

        return fetch;
    }
}