package com.sparta.filmfly.domain.reaction.repository;

import com.sparta.filmfly.domain.reaction.dto.ReactionBoardResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionCommentResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionMovieResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionReviewResponseDto;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface GoodRepositoryCustom {
    PageResponseDto<ReactionMovieResponseDto> getPageMovieByUserGood(Long userId, Pageable pageable);

    PageResponseDto<ReactionReviewResponseDto> getPageReviewByUserGood(Long userId, Pageable pageable);

    PageResponseDto<ReactionBoardResponseDto> getPageBoardByUserGood(Long userId, Pageable pageable);

    PageResponseDto<ReactionCommentResponseDto> getPageCommentByUserGood(Long userId, Pageable pageable);
}