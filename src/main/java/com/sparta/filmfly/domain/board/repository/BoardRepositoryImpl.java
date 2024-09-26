package com.sparta.filmfly.domain.board.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.filmfly.domain.board.dto.BoardPageDto;
import com.sparta.filmfly.domain.board.dto.BoardResponseDto;
import com.sparta.filmfly.domain.board.entity.Board;
import com.sparta.filmfly.domain.board.entity.QBoard;
import com.sparta.filmfly.domain.reaction.ReactionContentTypeEnum;
import com.sparta.filmfly.domain.reaction.dto.ReactionCheckResponseDto;
import com.sparta.filmfly.domain.reaction.entity.QBad;
import com.sparta.filmfly.domain.reaction.entity.QGood;
import com.sparta.filmfly.domain.user.entity.QUser;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
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
public class BoardRepositoryImpl implements BoardRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponseDto<BoardPageDto> findAllWithFilters(Pageable pageable, Long filterGoodCount, Long filterHits, String search) {
        QBoard board = QBoard.board;
        QGood good = QGood.good;
        QBad bad = QBad.bad;

        // 메인 쿼리에서 좋아요와 싫어요 개수를 계산하여 데이터를 조회
        JPQLQuery<BoardPageDto> query = queryFactory
            .select(Projections.constructor(BoardPageDto.class,
                board.id,
                board.user.id,
                board.title,
                board.user.nickname,
                board.createdAt,
                good.id.countDistinct().as("goodCount"),
                bad.id.countDistinct().as("badCount"),
                board.hits
            ))
            .from(board)
            .leftJoin(good).on(good.type.eq(ReactionContentTypeEnum.BOARD).and(good.typeId.eq(board.id)))
            .leftJoin(bad).on(bad.type.eq(ReactionContentTypeEnum.BOARD).and(bad.typeId.eq(board.id)))
            .groupBy(board.id, board.user.id, board.title, board.user.nickname, board.createdAt, board.hits)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize());

        // 좋아요 필터링 조건
        if (filterGoodCount != null) {
            query.having(good.id.countDistinct().goe(filterGoodCount));
        }
        // 조회수 필터링 조건
        if (filterHits != null) {
            query.where(board.hits.goe(filterHits));
        }
        // 검색어 필터링 조건
        if (search != null && !search.isEmpty()) {
            query.where(board.title.containsIgnoreCase(search));
        }

        // 생성 일자 기준 내림차순 정렬
        query.orderBy(board.createdAt.desc());

        // 페이징 처리된 결과 목록 가져오기
        List<BoardPageDto> content = query.fetch();
        long total = query.fetchCount();

        // PageImpl을 사용하여 페이지 정보를 생성합니다.
        PageImpl<BoardPageDto> page = new PageImpl<>(content, pageable, total);

        // PageResponseDto 반환합니다.
        return PageResponseDto.of(page);
    }


    @Override
    public PageResponseDto<BoardPageDto> findAllByUserId(Long userId, Pageable pageable) {
        QBoard board = QBoard.board;
        QGood good = QGood.good;
        QBad bad = QBad.bad;

        JPQLQuery<BoardPageDto> query = queryFactory
                .select(Projections.constructor(BoardPageDto.class,
                        board.id,
                        board.user.id,
                        board.title,
                        board.user.nickname,
                        board.createdAt,
                        good.id.countDistinct().as("goodCount"),
                        bad.id.countDistinct().as("badCount"),
                        board.hits
                ))
                .from(board)
                .leftJoin(good).on(good.type.eq(ReactionContentTypeEnum.BOARD).and(good.typeId.eq(board.id)))
                .leftJoin(bad).on(bad.type.eq(ReactionContentTypeEnum.BOARD).and(bad.typeId.eq(board.id)))
                .where(board.user.id.eq(userId))
                .groupBy(board.id, board.user.id, board.title, board.user.nickname, board.createdAt, board.hits)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(board.createdAt.desc()); // 기본 정렬 조건: 생성 일자 최신순

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
        .leftJoin(qGood).on(
            qGood.user.eq(user)
                .and(qBoard.id.eq(qGood.typeId))
                .and(qGood.type.eq(ReactionContentTypeEnum.BOARD))
        )
        .leftJoin(qBad).on(
            qBad.user.eq(user)
                .and(qBoard.id.eq(qBad.typeId))
                .and(qBad.type.eq(ReactionContentTypeEnum.BOARD))
        )
        .where(qBoard.id.eq(boardId))
        .fetchOne();

        return fetchOne;
    }

}