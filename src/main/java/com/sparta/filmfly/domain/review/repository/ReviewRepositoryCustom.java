package com.sparta.filmfly.domain.review.repository;

import com.sparta.filmfly.domain.review.dto.ReviewReactionCheckResponseDto;
import com.sparta.filmfly.domain.review.dto.ReviewResponseDto;
import com.sparta.filmfly.domain.review.dto.ReviewUserResponseDto;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewRepositoryCustom {
    PageResponseDto<ReviewResponseDto> getPageReviewByMovieId(Long movieId, Pageable pageable);
    PageResponseDto<ReviewUserResponseDto> getPageReviewByUserId(Long userId, Pageable pageable);

    Float getAverageRatingByMovieId(Long movieId);

    List<ReviewReactionCheckResponseDto> checkReviewReaction(User user, List<Long> reviewIds);

    List<ReviewResponseDto> findReviewsRecent(int limit);

}