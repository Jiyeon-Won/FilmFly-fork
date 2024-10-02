package com.sparta.filmfly.domain.board.repository;

import com.sparta.filmfly.domain.board.entity.Board;
import com.sparta.filmfly.global.common.batch.hardDelete.SoftDeletableRepository;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.detail.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom, SoftDeletableRepository<Board> {
    default Board findByIdOrElseThrow(Long boardId) {
        return findById(boardId)
                .orElseThrow(() -> new NotFoundException(ResponseCodeEnum.BOARD_NOT_FOUND));
    }
    default void existsByIdOrElseThrow(Long commentId) {
        if (!existsById(commentId)) {
            throw new NotFoundException(ResponseCodeEnum.BOARD_NOT_FOUND);
        }
    }

    long count();
}