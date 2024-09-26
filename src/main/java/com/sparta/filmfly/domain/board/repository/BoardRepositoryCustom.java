package com.sparta.filmfly.domain.board.repository;

import com.sparta.filmfly.domain.board.dto.BoardPageDto;
import com.sparta.filmfly.domain.board.dto.BoardResponseDto;
import com.sparta.filmfly.domain.reaction.dto.ReactionCheckResponseDto;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardRepositoryCustom {
    PageResponseDto<BoardPageDto> findAllWithFilters(Pageable pageable, Long filterGoodCount, Long filterHits, String search);
    PageResponseDto<BoardPageDto> findAllByUserId(Long userId,Pageable pageable);

    BoardResponseDto getBoard(Long boardId);


    ReactionCheckResponseDto checkBoardReaction(User user, Long boardId);
}