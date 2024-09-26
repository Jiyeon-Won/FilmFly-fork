package com.sparta.filmfly.domain.review.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.filmfly.domain.block.entity.QBlock;
import com.sparta.filmfly.domain.board.dto.BoardPageDto;
import com.sparta.filmfly.domain.board.entity.QBoard;
import com.sparta.filmfly.domain.reaction.ReactionContentTypeEnum;
import com.sparta.filmfly.domain.reaction.entity.QBad;
import com.sparta.filmfly.domain.reaction.entity.QGood;
import com.sparta.filmfly.domain.review.dto.ReviewReactionCheckResponseDto;
import com.sparta.filmfly.domain.review.dto.ReviewResponseDto;
import com.sparta.filmfly.domain.review.dto.ReviewUserResponseDto;
import com.sparta.filmfly.domain.review.entity.QReview;
import com.sparta.filmfly.domain.review.entity.Review;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 특정 영화에 대한 리뷰 전체 조회
     */
    @Override
    public PageResponseDto<ReviewResponseDto> getPageReviewByMovieId(Long movieId, Pageable pageable) {
        QReview qReview = QReview.review;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        List<ReviewResponseDto> fetch = queryFactory.select(Projections.constructor(
                ReviewResponseDto.class,
                qReview.id,
                qReview.user.id,
                qReview.movie.id,
                qReview.movie.title,
                qReview.user.nickname,
                qReview.user.pictureUrl,
                qReview.rating,
                qReview.title,
                qReview.content,
                qReview.createdAt,
                qGood.id.countDistinct().as("goodCount"),
                qBad.id.countDistinct().as("badCount")
            ))
            .from(qReview)
            .leftJoin(qGood).on(qGood.type.eq(ReactionContentTypeEnum.REVIEW)
                .and(qGood.typeId.eq(qReview.id)))
            .leftJoin(qBad).on(qBad.type.eq(ReactionContentTypeEnum.REVIEW)
                .and(qBad.typeId.eq(qReview.id)))
            .where(qReview.movie.id.eq(movieId))
            .groupBy(qReview.id)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(qReview.createdAt.desc())
            .fetch();

        Long total = queryFactory
            .select(qReview.count())
            .from(qReview)
            .where(qReview.movie.id.eq(movieId))
            .fetchOne();

        PageImpl<ReviewResponseDto> page = new PageImpl<>(fetch, pageable, total);

        return PageResponseDto.of(page);
    }

    /**
     * 유저의 리뷰 목록
     */
    @Override
    public PageResponseDto<ReviewUserResponseDto> getPageReviewByUserId(Long userId, Pageable pageable) {
        QReview qReview = QReview.review;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        List<ReviewUserResponseDto> fetch = queryFactory
            .select(Projections.constructor(
                ReviewUserResponseDto.class,
                qReview.id,
                qReview.user.id,
                qReview.movie.id,
                qReview.movie.title,
                qReview.user.nickname,
                qReview.user.pictureUrl,
                qReview.rating,
                qReview.title,
                qReview.content,
                qReview.createdAt,
                qGood.id.countDistinct().as("goodCount"),
                qBad.id.countDistinct().as("badCount")
            ))
            .from(qReview)
            .leftJoin(qGood).on(qGood.type.eq(ReactionContentTypeEnum.REVIEW)
                .and(qGood.typeId.eq(qReview.id)))
            .leftJoin(qBad).on(qBad.type.eq(ReactionContentTypeEnum.REVIEW)
                .and(qBad.typeId.eq(qReview.id)))
            .where(qReview.user.id.eq(userId))
            .groupBy(qReview.id)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(qReview.createdAt.desc())
            .fetch();

        Long total = queryFactory
            .select(qReview.count())
            .from(qReview)
            .where(qReview.user.id.eq(userId))
            .fetchOne();

        PageImpl<ReviewUserResponseDto> page = new PageImpl<>(fetch, pageable, total);

        return PageResponseDto.of(page);
    }

    /**
     * 특정 영화의 평균 평점 조회
     */
    @Override
    public Float getAverageRatingByMovieId(Long movieId) {
        QReview qReview = QReview.review;

        Double averageRating = queryFactory
            .select(qReview.rating.avg())
            .from(qReview)
            .where(qReview.movie.id.eq(movieId)
                .and(qReview.deletedAt.isNull())) // 조건을 추가하여 삭제된 리뷰를 제외
            .fetchOne();

        if (averageRating == null) {
            averageRating = 0.0;
        }

        return Math.round(averageRating * 10) / 10.0f;
    }

    /**
     * 리뷰 조회 시 해당 사용자가 리뷰에 대해 좋아요, 싫어요, 차단을 눌렀는지 확인하는 코드
     */
    @Override
    public List<ReviewReactionCheckResponseDto> checkReviewReaction(User user, List<Long> reviewIds) {
        QReview qReview = QReview.review;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;
        QBlock qBlock = QBlock.block;

        List<ReviewReactionCheckResponseDto> fetch = queryFactory
            .select(
                Projections.constructor(
                    ReviewReactionCheckResponseDto.class,
                    qReview.id,
                    qGood.id.isNotNull(),
                    qBad.id.isNotNull(),
                    qBlock.id.isNotNull()
                ))
            .from(qReview)
            .leftJoin(qGood).on(
                qGood.user.eq(user)
                    .and(qReview.id.eq(qGood.typeId))
                    .and(qGood.type.eq(ReactionContentTypeEnum.REVIEW))
            )
            .leftJoin(qBad).on(
                qBad.user.eq(user)
                    .and(qReview.id.eq(qBad.typeId))
                    .and(qBad.type.eq(ReactionContentTypeEnum.REVIEW))
            )
            .leftJoin(qBlock).on(
                qBlock.blocker.eq(user)
                    .and(qReview.user.id.eq(qBlock.blocked.id))
            )
            .where(qReview.id.in(reviewIds))
            .orderBy(qReview.createdAt.desc())
            .fetch();

        return fetch;
    }

    @Override
    public PageResponseDto<ReviewResponseDto> findAllWithFilters(Pageable pageable, Long filterGoodCount, String search) {
        QReview review = QReview.review;
        QGood good = QGood.good;
        QBad bad = QBad.bad;
        Review a;
        // 메인 쿼리에서 좋아요와 싫어요 개수를 계산하여 데이터를 조회
        JPQLQuery<ReviewResponseDto> query = queryFactory
                .select(Projections.constructor(ReviewResponseDto.class,
                        review.id,
                        review.user.id,
                        review.movie.id,
                        review.movie.title,
                        review.user.nickname,
                        review.user.pictureUrl,
                        review.rating,
                        review.title,
                        review.content,
                        review.createdAt,
                        good.id.countDistinct().as("goodCount"),
                        bad.id.countDistinct().as("badCount")
                ))
                .from(review)
                .leftJoin(good).on(good.type.eq(ReactionContentTypeEnum.REVIEW).and(good.typeId.eq(review.id)))
                .leftJoin(bad).on(bad.type.eq(ReactionContentTypeEnum.REVIEW).and(bad.typeId.eq(review.id)))
                .groupBy(review.id, review.user.id, review.title, review.user.nickname, review.createdAt)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 좋아요 필터링 조건
        if (filterGoodCount != null) {
            query.having(good.id.countDistinct().goe(filterGoodCount));
        }
        // 검색어 필터링 조건
        if (search != null && !search.isEmpty()) {
            query.where(review.title.containsIgnoreCase(search));
        }

        // 생성 일자 기준 내림차순 정렬
        query.orderBy(review.createdAt.desc());

        // 페이징 처리된 결과 목록 가져오기
        List<ReviewResponseDto> content = query.fetch();
        long total = query.fetchCount();

        // PageImpl을 사용하여 페이지 정보를 생성합니다.
        PageImpl<ReviewResponseDto> page = new PageImpl<>(content, pageable, total);

        // PageResponseDto 반환합니다.
        return PageResponseDto.of(page);
    }
}