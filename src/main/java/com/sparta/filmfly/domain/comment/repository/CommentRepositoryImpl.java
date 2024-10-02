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
import com.sparta.filmfly.domain.user.entity.QUser;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.common.response.PageResponseDto;
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
        QComment qComment = QComment.comment;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        List<CommentResponseDto> fetch = queryFactory
            .select(Projections.constructor(CommentResponseDto.class,
                qComment.id,
                qComment.user.id,
                qComment.board.id,
                qComment.user.nickname,
                qComment.user.pictureUrl,
                qComment.content,
                qComment.createdAt,
                qGood.id.countDistinct().as("goodCount"),
                qBad.id.countDistinct().as("badCount")
            ))
            .from(qComment)
            .leftJoin(qGood).on(qGood.typeId.eq(qComment.id)
                .and(qGood.type.eq(ReactionContentTypeEnum.COMMENT))
            )
            .leftJoin(qBad).on(qBad.typeId.eq(qComment.id)
                .and(qBad.type.eq(ReactionContentTypeEnum.COMMENT))
            )
            .where(qComment.board.id.eq(boardId))
            .groupBy(qComment.id, qComment.user.id, qComment.user.nickname, qComment.content, qComment.createdAt)
            .orderBy(qComment.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 페이지의 총 요소 수를 가져옵니다.
        Long total = queryFactory
            .select(qComment.count())
            .from(qComment)
            .where(qComment.board.id.eq(boardId))
            .fetchOne();

        // PageImpl을 사용하여 페이지 정보를 생성합니다.
        PageImpl<CommentResponseDto> page = new PageImpl<>(fetch, pageable, total);

        // PageResponseDto 반환합니다.
        return PageResponseDto.of(page);
    }

    @Override
    public PageResponseDto<CommentResponseDto> findAllByUserId(Long userId, Pageable pageable) {
        QComment qComment = QComment.comment;
        QUser qUser = QUser.user;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        JPQLQuery<CommentResponseDto> query = queryFactory
            .select(Projections.constructor(CommentResponseDto.class,
                    qComment.id,
                    qUser.id,
                    qComment.board.id,
                    qUser.nickname,
                    qUser.pictureUrl,
                    qComment.content,
                    qComment.updatedAt,
                    qGood.id.countDistinct().as("goodCount"),
                    qBad.id.countDistinct().as("badCount")
            ))
            .from(qComment)
            .join(qUser).on(qUser.id.eq(qComment.user.id))
            .leftJoin(qGood).on(qGood.typeId.eq(qComment.id)
                .and(qGood.type.eq(ReactionContentTypeEnum.COMMENT))
            )
            .leftJoin(qBad).on(qBad.typeId.eq(qComment.id)
                .and(qBad.type.eq(ReactionContentTypeEnum.COMMENT))
            )
            .where(qComment.user.id.eq(userId))
            .groupBy(qComment.createdAt, qComment.id)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(qComment.createdAt.desc(), qComment.id.desc()); // 기본 정렬 조건: 생성 일자 최신순

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
            .leftJoin(qGood).on(qGood.user.eq(user)
                    .and(qComment.id.eq(qGood.typeId))
                    .and(qGood.type.eq(ReactionContentTypeEnum.COMMENT))
            )
            .leftJoin(qBad).on(qBad.user.eq(user)
                    .and(qComment.id.eq(qBad.typeId))
                    .and(qBad.type.eq(ReactionContentTypeEnum.COMMENT))
            )
            .where(qComment.id.in(commentIds))
            .orderBy(qComment.createdAt.desc())
            .fetch();

        return fetch;
    }
}