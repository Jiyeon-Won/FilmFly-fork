package com.sparta.filmfly.domain.reaction.service;

import com.sparta.filmfly.domain.reaction.ReactionContentTypeEnum;
import com.sparta.filmfly.domain.reaction.dto.BadRequestDto;
import com.sparta.filmfly.domain.reaction.dto.BadResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionBoardResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionCommentResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionMovieResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionReviewResponseDto;
import com.sparta.filmfly.domain.reaction.entity.Bad;
import com.sparta.filmfly.domain.reaction.repository.BadRepository;
import com.sparta.filmfly.domain.reaction.repository.GoodRepository;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadService {

    private final ReactionService reactionService;
    private final BadRepository badRepository;
    private final GoodRepository goodRepository;
    /**
     * 싫어요 추가
     */
    @Transactional
    public BadResponseDto addBad(User loginUser, BadRequestDto requestDto) {
        ReactionContentTypeEnum contentType = requestDto.getContentType();
        Long contentId = requestDto.getContentId();

        reactionService.checkContentExist(contentType,contentId);

        // 이미 싫어요가 등록되어 있으면 예외
        badRepository.existsByTypeIdAndTypeAndUserIdOrElseThrow(
                contentId, contentType, loginUser.getId()
        );

        // 좋아요와 싫어요를 동시에 누를 수 없음 => 좋아요 등록한 상태면 싫어요 추가X
        goodRepository.existsByTypeIdAndTypeAndUserIdOrElseThrow(
                contentId, contentType, loginUser.getId()
        );

        Bad bad = requestDto.toEntity(loginUser, contentType);
        badRepository.save(bad);
        return BadResponseDto.fromEntity(bad);
    }

    /**
     * 싫어요 취소
     */
    @Transactional
    public BadResponseDto removeBad(User loginUser, BadRequestDto requestDto) {
        ReactionContentTypeEnum contentType = requestDto.getContentType();
        Long contentId = requestDto.getContentId();

        reactionService.checkContentExist(contentType,contentId);

        // 싫어요가 없으면 예외
        Bad bad = badRepository.findByTypeIdAndTypeAndUserIdOrElseThrow(
                contentId, contentType, loginUser.getId()
        );

        badRepository.delete(bad);
        return BadResponseDto.fromEntity(bad);
    }

    /**
     * 싫어요 카운트
     */
    public Long getCountByTypeTypeId(ReactionContentTypeEnum type, Long typeId) {
        return badRepository.countByTypeAndTypeId(type,typeId);
    }

    /**
     * 싫어요한 유저인지 확인
     */
    public boolean checkBadByUser(User loginUser, BadRequestDto requestDto) {
        return badRepository.existsByTypeIdAndTypeAndUserId(
                requestDto.getContentId(), requestDto.getContentType(), loginUser.getId()
        );
    }

    /**
     * 사용자가 싫어요를 누른 영화 조회
     */
    public PageResponseDto<ReactionMovieResponseDto> getPageBadMovie(Long userId, Pageable pageable) {
        return badRepository.getPageMovieByUserBad(userId, pageable);
    }

    /**
     * 사용자가 싫어요를 누른 리뷰 조회
     */
    public PageResponseDto<ReactionReviewResponseDto> getPageBadReview(Long userId, Pageable pageable) {
        return badRepository.getPageReviewByUserBad(userId, pageable);
    }

    /**
     * 사용자가 싫어요를 누른 게시물 조회
     */
    public PageResponseDto<ReactionBoardResponseDto> getPageBadBoard(Long userId, Pageable pageable) {
        return badRepository.getPageBoardByUserBad(userId, pageable);
    }

    /**
     * 사용자가 싫어요를 누른 댓글 조회
     */
    public PageResponseDto<ReactionCommentResponseDto> getPageBadComment(Long userId, Pageable pageable) {
        return badRepository.getPageCommentByUserBad(userId, pageable);
    }
}