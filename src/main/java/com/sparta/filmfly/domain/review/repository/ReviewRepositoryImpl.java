package com.sparta.filmfly.domain.review.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.filmfly.domain.block.entity.QBlock;
import com.sparta.filmfly.domain.movie.entity.QMovie;
import com.sparta.filmfly.domain.reaction.ReactionContentTypeEnum;
import com.sparta.filmfly.domain.reaction.entity.QBad;
import com.sparta.filmfly.domain.reaction.entity.QGood;
import com.sparta.filmfly.domain.review.dto.ReviewReactionCheckResponseDto;
import com.sparta.filmfly.domain.review.dto.ReviewResponseDto;
import com.sparta.filmfly.domain.review.dto.ReviewUserResponseDto;
import com.sparta.filmfly.domain.review.entity.QReview;
import com.sparta.filmfly.domain.user.entity.QUser;
import com.sparta.filmfly.domain.user.entity.User;
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
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 특정 영화에 대한 리뷰 전체 조회
     */
    @Override
    public PageResponseDto<ReviewResponseDto> getPageReviewByMovieId(Long movieId, Pageable pageable) {
        QReview qReview = QReview.review;
        QMovie qMovie = QMovie.movie;
        QUser qUser = QUser.user;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        List<ReviewResponseDto> fetch = queryFactory.select(Projections.constructor(
                ReviewResponseDto.class,
                qReview.id,
                qUser.id,
                qMovie.id,
                qMovie.title,
                qUser.nickname,
                qUser.pictureUrl,
                qReview.rating,
                qReview.title,
                qReview.content,
                qReview.createdAt,
                qGood.id.countDistinct().as("goodCount"),
                qBad.id.countDistinct().as("badCount")
            ))
            .from(qReview)
            .join(qUser).on(qUser.id.eq(qReview.user.id))
            .join(qMovie).on(qMovie.id.eq(qReview.movie.id))
            .leftJoin(qGood).on(qGood.typeId.eq(qReview.id)
                .and(qGood.type.eq(ReactionContentTypeEnum.REVIEW))
            )
            .leftJoin(qBad).on(qBad.typeId.eq(qReview.id)
                .and(qBad.type.eq(ReactionContentTypeEnum.REVIEW))
            )
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
        QMovie qMovie = QMovie.movie;
        QUser qUser = QUser.user;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        List<ReviewUserResponseDto> fetch = queryFactory
            .select(Projections.constructor(
                ReviewUserResponseDto.class,
                qReview.id,
                qUser.id,
                qMovie.id,
                qMovie.title,
                qUser.nickname,
                qUser.pictureUrl,
                qReview.rating,
                qReview.title,
                qReview.content,
                qReview.createdAt,
                qGood.id.countDistinct().as("goodCount"),
                qBad.id.countDistinct().as("badCount")
            ))
            .from(qReview)
            .join(qUser).on(qUser.id.eq(qReview.user.id))
            .join(qMovie).on(qMovie.id.eq(qReview.movie.id))
            .leftJoin(qGood).on(qGood.typeId.eq(qReview.id)
                .and(qGood.type.eq(ReactionContentTypeEnum.REVIEW))
            )
            .leftJoin(qBad).on(qBad.typeId.eq(qReview.id)
                .and(qBad.type.eq(ReactionContentTypeEnum.REVIEW))
            )
            .where(qReview.user.id.eq(userId))
            .groupBy(qReview.createdAt, qReview.id)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(qReview.createdAt.desc(), qReview.id.desc())
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
            .leftJoin(qBlock).on(
                qBlock.blocker.eq(user)
                    .and(qReview.user.id.eq(qBlock.blocked.id))
            )
            .leftJoin(qGood).on(qGood.user.eq(user)
                    .and(qReview.id.eq(qGood.typeId))
                    .and(qGood.type.eq(ReactionContentTypeEnum.REVIEW))
            )
            .leftJoin(qBad).on(qBad.user.eq(user)
                    .and(qReview.id.eq(qBad.typeId))
                    .and(qBad.type.eq(ReactionContentTypeEnum.REVIEW))
            )
            .where(qReview.id.in(reviewIds))
            .orderBy(qReview.createdAt.desc())
            .fetch();

        return fetch;
    }

    @Override
    public List<ReviewResponseDto> findReviewsRecent(int limit) {
        QReview qReview = QReview.review;
        QMovie qMovie = QMovie.movie;
        QGood qGood = QGood.good;
        QBad qBad = QBad.bad;

        List<ReviewResponseDto> fetch = queryFactory
            .select(Projections.constructor(ReviewResponseDto.class,
                qReview.id,
                qReview.movie.id,
                qMovie.title,
                qReview.rating,
                qReview.title,
                qReview.content,
                qReview.createdAt,
                qGood.id.countDistinct().as("goodCount"),
                qBad.id.countDistinct().as("badCount")
            ))
            .from(qReview)
            .join(qMovie).on(qMovie.id.eq(qReview.movie.id))
            .leftJoin(qGood).on(qGood.typeId.eq(qReview.id)
                .and(qGood.type.eq(ReactionContentTypeEnum.REVIEW))
            )
            .leftJoin(qBad).on(qBad.typeId.eq(qReview.id)
                .and(qBad.type.eq(ReactionContentTypeEnum.REVIEW))
            )
            .groupBy(qReview.createdAt, qReview.id)
            .orderBy(qReview.createdAt.desc(), qReview.id.desc())
            .limit(limit)
            .fetch();

        return fetch;
    }
}