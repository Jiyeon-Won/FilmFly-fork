package com.sparta.filmfly.domain.comment.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.filmfly.domain.comment.dto.CommentPageResponseDto;
import com.sparta.filmfly.domain.comment.dto.CommentResponseDto;
import com.sparta.filmfly.domain.comment.entity.QComment;
import com.sparta.filmfly.domain.reaction.ReactionContentTypeEnum;
import com.sparta.filmfly.domain.reaction.entity.QBad;
import com.sparta.filmfly.domain.reaction.entity.QGood;
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

    public CommentPageResponseDto findAllByBoardIdWithReactions(Long boardId, Pageable pageable) {
        QComment comment = QComment.comment;
        QGood good = QGood.good;
        QBad bad = QBad.bad;

        JPQLQuery<CommentResponseDto> query = queryFactory
                .select(Projections.constructor(CommentResponseDto.class,
                        comment.id,
                        comment.user.id,
                        comment.user.nickname,
                        comment.content,
                        comment.updatedAt,
                        good.id.count().as("goodCount"),
                        bad.id.count().as("badCount")
                ))
                .from(comment)
                .leftJoin(good).on(good.type.eq(ReactionContentTypeEnum.COMMENT).and(good.typeId.eq(comment.id)))
                .leftJoin(bad).on(bad.type.eq(ReactionContentTypeEnum.COMMENT).and(bad.typeId.eq(comment.id)))
                .where(comment.board.id.eq(boardId))
                .groupBy(comment.id, comment.user.id, comment.user.nickname, comment.content, comment.updatedAt)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<CommentResponseDto> content = query.fetch();
        long total = query.fetchCount();  // 페이지의 총 요소 수를 가져옵니다.

        // PageImpl을 사용하여 페이지 정보를 생성합니다.
        PageImpl<CommentResponseDto> page = new PageImpl<>(content, pageable, total);

        // CommentPageResponseDto를 반환합니다.
        return CommentPageResponseDto.builder()
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .currentPages(page.getNumber() + 1)
                .size(page.getSize())
                .content(content)
                .build();
    }

    public CommentPageResponseDto findAllByUserId(Long userId, Pageable pageable) {
        QComment comment = QComment.comment;
        QGood good = QGood.good;
        QBad bad = QBad.bad;

        JPQLQuery<CommentResponseDto> query = queryFactory
                .select(Projections.constructor(CommentResponseDto.class,
                        comment.id,
                        comment.user.id,
                        comment.user.nickname,
                        comment.content,
                        comment.updatedAt,
                        good.id.count().as("goodCount"),
                        bad.id.count().as("badCount")
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

        // CommentPageResponseDto를 반환합니다.
        return CommentPageResponseDto.builder()
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .currentPages(page.getNumber() + 1)
                .size(page.getSize())
                .content(content)
                .build();
    }
}