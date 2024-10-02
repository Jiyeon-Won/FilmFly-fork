package com.sparta.filmfly.domain.board.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.filmfly.domain.board.dto.BoardPageDto;
import com.sparta.filmfly.domain.board.dto.BoardResponseDto;
import com.sparta.filmfly.domain.board.entity.QBoard;
import com.sparta.filmfly.domain.reaction.ReactionContentTypeEnum;
import com.sparta.filmfly.domain.reaction.dto.ReactionCheckResponseDto;
import com.sparta.filmfly.domain.reaction.entity.QBad;
import com.sparta.filmfly.domain.reaction.entity.QGood;
import com.sparta.filmfly.domain.user.entity.QUser;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponseDto<BoardPageDto> findAllWithFilters(Pageable pageable, Long filterGoodCount, Long filterHits, String search) {
        QBoard qBoard = QBoard.board;
        QUser qUser = QUser.user;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        // 메인 쿼리에서 좋아요와 싫어요 개수를 계산하여 데이터를 조회
        List<BoardPageDto> fetch = queryFactory
            .select(Projections.constructor(BoardPageDto.class,
                qBoard.id,
                qUser.id,
                qBoard.title,
                qUser.nickname,
                qBoard.createdAt,
                qGood.id.countDistinct().as("goodCount"),
                qBad.id.countDistinct().as("badCount"),
                qBoard.hits
            ))
            .from(qBoard)
            .join(qUser).on(qUser.id.eq(qBoard.user.id))
            .leftJoin(qGood).on(qGood.typeId.eq(qBoard.id)
                .and(qGood.type.eq(ReactionContentTypeEnum.BOARD))
            )
            .leftJoin(qBad).on(qBad.typeId.eq(qBoard.id)
                .and(qBad.type.eq(ReactionContentTypeEnum.BOARD))
            )
            .where(searchPredicate(filterHits, search, qBoard))
            .groupBy(qBoard.createdAt, qBoard.id)
            .having(havingGoodCount(filterGoodCount, qGood))
            .orderBy(qBoard.createdAt.desc(), qBoard.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPQLQuery<Long> subquery = JPAExpressions
            .select(qBoard.id)
            .from(qBoard)
            .join(qUser).on(qUser.id.eq(qBoard.user.id))
            .leftJoin(qGood).on(qGood.typeId.eq(qBoard.id)
                .and(qGood.type.eq(ReactionContentTypeEnum.BOARD))
            )
            .leftJoin(qBad).on(qBad.typeId.eq(qBoard.id)
                .and(qBad.type.eq(ReactionContentTypeEnum.BOARD))
            )
            .where(searchPredicate(filterHits, search, qBoard))
            .groupBy(qBoard.id)
            .having(havingGoodCount(filterGoodCount, qGood));

        JPAQuery<Long> where = queryFactory
            .select(qBoard.id.count())
            .from(qBoard)
            .where(qBoard.id.in(subquery));

        return PageResponseDto.of(
            PageableExecutionUtils.getPage(fetch, pageable, where::fetchOne)
        );
    }


    @Override
    public PageResponseDto<BoardPageDto> findAllByUserId(Long userId, Pageable pageable) {
        QBoard qBoard = QBoard.board;
        QUser qUser = QUser.user;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        JPQLQuery<BoardPageDto> query = queryFactory
            .select(Projections.constructor(BoardPageDto.class,
                qBoard.id,
                qUser.id,
                qBoard.title,
                qUser.nickname,
                qBoard.createdAt,
                qGood.id.countDistinct().as("goodCount"),
                qBad.id.countDistinct().as("badCount"),
                qBoard.hits
            ))
            .from(qBoard)
            .join(qUser).on(qUser.id.eq(qBoard.user.id))
            .leftJoin(qGood).on(qGood.typeId.eq(qBoard.id)
                .and(qGood.type.eq(ReactionContentTypeEnum.BOARD))
            )
            .leftJoin(qBad).on(qBad.typeId.eq(qBoard.id)
                .and(qBad.type.eq(ReactionContentTypeEnum.BOARD))
            )
            .where(qBoard.user.id.eq(userId))
            .groupBy(qBoard.createdAt, qBoard.id)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(qBoard.createdAt.desc(), qBoard.id.desc()); // 기본 정렬 조건: 생성 일자 최신순

        List<BoardPageDto> content = query.fetch();
        long total = query.fetchCount();  // 페이지의 총 요소 수를 가져옵니다.

        // PageImpl을 사용하여 페이지 정보를 생성합니다.
        PageImpl<BoardPageDto> page = new PageImpl<>(content, pageable, total);

        // PageResponseDto 반환합니다.
        return PageResponseDto.of(page);
    }

    @Override
    public BoardResponseDto getBoard(Long boardId) {
        QBoard qBoard = QBoard.board;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        BoardResponseDto fetch = queryFactory.select(
                Projections.constructor(
                    BoardResponseDto.class,
                    qBoard.id,
                    qBoard.user.id,
                    qBoard.title,
                    qBoard.content,
                    qBoard.user.nickname,
                    qBoard.user.pictureUrl,
                    qBoard.createdAt,
                    qGood.id.countDistinct().as("goodCount"),
                    qBad.id.countDistinct().as("badCount"),
                    qBoard.hits
                )
            )
            .from(qBoard)
            .leftJoin(qGood).on(qGood.typeId.eq(qBoard.id)
                .and(qGood.type.eq(ReactionContentTypeEnum.BOARD))
            )
            .leftJoin(qBad).on(qBad.typeId.eq(qBoard.id)
                .and(qBad.type.eq(ReactionContentTypeEnum.BOARD))
            )
            .where(qBoard.id.eq(boardId))
            .groupBy(qBoard.id)
            .fetchOne();

        return fetch;
    }

    @Override
    public ReactionCheckResponseDto checkBoardReaction(User user, Long boardId) {
        QBoard qBoard = QBoard.board;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        ReactionCheckResponseDto fetchOne = queryFactory.select(
            Projections.constructor(
                ReactionCheckResponseDto.class,
                qGood.id.isNotNull(),
                qBad.id.isNotNull()
            )
        )
        .from(qBoard)
        .leftJoin(qGood).on(qGood.user.eq(user)
                .and(qBoard.id.eq(qGood.typeId))
                .and(qGood.type.eq(ReactionContentTypeEnum.BOARD))
        )
        .leftJoin(qBad).on(qBad.user.eq(user)
                .and(qBoard.id.eq(qBad.typeId))
                .and(qBad.type.eq(ReactionContentTypeEnum.BOARD))
        )
        .where(qBoard.id.eq(boardId))
        .fetchOne();

        return fetchOne;
    }

    @Override
    public List<BoardResponseDto> findBoardsRecent(int limit) {
        QBoard qBoard = QBoard.board;
        QUser qUser = QUser.user;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        List<BoardResponseDto> fetch = queryFactory
            .select(Projections.constructor(BoardResponseDto.class,
                qBoard.id,
                qUser.id,
                qBoard.title,
                qBoard.content,
                qUser.nickname,
                qUser.pictureUrl,
                qBoard.createdAt,
                qGood.id.countDistinct().as("goodCount"),
                qBad.id.countDistinct().as("badCount"),
                qBoard.hits
            ))
            .from(qBoard)
            .join(qUser).on(qUser.id.eq(qBoard.user.id))
            .leftJoin(qGood).on(qGood.typeId.eq(qBoard.id)
                .and(qGood.type.eq(ReactionContentTypeEnum.BOARD))
            )
            .leftJoin(qBad).on(qBad.typeId.eq(qBoard.id)
                .and(qBad.type.eq(ReactionContentTypeEnum.BOARD))
            )
            .groupBy(qBoard.createdAt, qBoard.id)
            .orderBy(qBoard.createdAt.desc(), qBoard.id.desc())
            .limit(limit)
            .fetch();

        return fetch;
    }

    private BooleanExpression havingGoodCount(Long filterGoodCount, QGood qGood) {
        if (filterGoodCount != null && filterGoodCount > 0) {
            return qGood.id.countDistinct().goe(filterGoodCount);
        }
        return null;
    }

    private BooleanExpression searchPredicate(Long filterHits, String keyword, QBoard qBoard) {
        BooleanExpression predicate;

        predicate = searchFilterHits(filterHits, qBoard);
        predicate = (predicate == null) ? searchKeyword(keyword, qBoard) : predicate.and(searchKeyword(keyword, qBoard));

        return predicate;
    }

    private BooleanExpression searchFilterHits(Long filterHits, QBoard qBoard) {
        if (filterHits != null && filterHits > 0) {
            return qBoard.hits.goe(filterHits);
        }
        return null;
    }

    private BooleanExpression searchKeyword(String keyword, QBoard qBoard) {
        if (StringUtils.hasText(keyword)) {
            return qBoard.title.like("%" + keyword + "%");
        }
        return null;
    }
}