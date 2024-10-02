package com.sparta.filmfly.domain.comment.repository;

import com.sparta.filmfly.domain.comment.dto.CommentResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionCheckResponseDto;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom {
    PageResponseDto<CommentResponseDto> findAllByBoardIdWithReactions(Long boardId, Pageable pageable);
    PageResponseDto<CommentResponseDto> findAllByUserId(Long userId, Pageable pageable);

    List<ReactionCheckResponseDto> checkCommentReaction(User user, List<Long> commentIds);
}