package com.sparta.filmfly.domain.comment.service;

import com.sparta.filmfly.domain.board.entity.Board;
import com.sparta.filmfly.domain.board.repository.BoardRepository;
import com.sparta.filmfly.domain.comment.dto.CommentRequestDto;
import com.sparta.filmfly.domain.comment.dto.CommentResponseDto;
import com.sparta.filmfly.domain.comment.dto.CommentUpdateResponseDto;
import com.sparta.filmfly.domain.comment.entity.Comment;
import com.sparta.filmfly.domain.comment.repository.CommentRepository;
import com.sparta.filmfly.domain.reaction.ReactionContentTypeEnum;
import com.sparta.filmfly.domain.reaction.dto.ReactionCheckResponseDto;
import com.sparta.filmfly.domain.reaction.service.BadService;
import com.sparta.filmfly.domain.reaction.service.GoodService;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.auth.UserDetailsImpl;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final GoodService goodService;
    private final BadService badService;

    /**
     * 댓글 생성
     */
    @Transactional
    public CommentResponseDto createComment(User user, CommentRequestDto requestDto, Long boardId) {
        Board board = boardRepository.findByIdOrElseThrow(boardId);

        Comment entity = requestDto.toEntity(user,board);
        Comment savedComment = commentRepository.save(entity);
        return CommentResponseDto.fromEntity(savedComment,0L,0L);
    }

    /**
     * 댓글 조회
     */
    @Transactional(readOnly = true)
    public CommentResponseDto getComment(Long commentId) {
        Comment comment = commentRepository.findByIdOrElseThrow(commentId);

        Long goodCount = goodService.getCountByTypeTypeId(ReactionContentTypeEnum.COMMENT,commentId);
        Long badCount = badService.getCountByTypeTypeId(ReactionContentTypeEnum.COMMENT,commentId);
        return CommentResponseDto.fromEntity(comment,goodCount,badCount);
    }

    /**
     * 댓글 페이지 조회
     */
    @Transactional(readOnly = true)
    public PageResponseDto<CommentResponseDto> gerPageComment(UserDetailsImpl userDetails, Long boardId, Pageable pageable) {
        //정렬은 생성 시간, get으로 넘겨주는 댓글은 수정 시간
        boardRepository.existsByIdOrElseThrow(boardId); // 보드가 존재하지 않으면 에러

        PageResponseDto<CommentResponseDto> pageComments = commentRepository.findAllByBoardIdWithReactions(boardId, pageable);
        if (userDetails != null) {
            List<CommentResponseDto> comments = pageComments.getData();
            List<Long> commentIds = comments.stream().map(CommentResponseDto::getId).toList();
            List<ReactionCheckResponseDto> reactions = commentRepository.checkCommentReaction(userDetails.getUser(), commentIds);
            for (int i = 0; i < comments.size(); ++i) {
                comments.get(i).setReactions(reactions.get(i));

                if (Objects.equals(comments.get(i).getUserId(), userDetails.getUser().getId())) {
                    comments.get(i).setOwner(true);
                }
            }
        }
        return pageComments;
    }

    /**
     * 유저의 댓글 조회
     */
    public PageResponseDto<CommentResponseDto> getUsersComments(UserDetailsImpl userDetails, Long userId, Pageable pageable) {
        PageResponseDto<CommentResponseDto> pageComment = commentRepository.findAllByUserId(userId, pageable);

        if (userDetails != null) {
            List<CommentResponseDto> comments = pageComment.getData();
            for (CommentResponseDto comment : comments) {
                if (Objects.equals(userDetails.getUser().getId(), comment.getUserId())) {
                    comment.setOwner(true);
                }
            }
        }

        return pageComment;
    }

    /**
     * 댓글 수정 권한 확인
     */
    public Boolean getCommentUpdatePermission(User user, Long commentId) {
        Comment comment = commentRepository.findByIdOrElseThrow(commentId);
        //admin이면 true 반환
        if(!user.isAdmin()) {
            comment.checkOwnerUser(user);
        }
        return true; //수정 권한 없으면 에러?
    }

    /**
     * 댓글 수정 정보
     */
    public CommentUpdateResponseDto forUpdateComment(User user, Long commentId) {
        Comment comment = commentRepository.findByIdOrElseThrow(commentId);
        comment.checkOwnerUser(user);
        return CommentUpdateResponseDto.fromEntity(comment);
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public CommentResponseDto updateComment(User user, CommentRequestDto requestDto, Long commentId) {
        Comment comment = commentRepository.findByIdOrElseThrow(commentId);
        comment.checkOwnerUser(user);

        comment.update(requestDto);
        Comment updatedComment = commentRepository.save(comment);
        Long goodCount = goodService.getCountByTypeTypeId(ReactionContentTypeEnum.COMMENT,commentId);
        Long badCount = badService.getCountByTypeTypeId(ReactionContentTypeEnum.COMMENT,commentId);

        return CommentResponseDto.fromEntity(updatedComment,goodCount,badCount);
    }
    /**
     * 댓글 삭제
     */
    @Transactional
    public String deleteComment(User user, Long commentId) {
        Comment comment = commentRepository.findByIdOrElseThrow(commentId);

        if(!user.isAdmin()) { //관리자면 삭제 가능하게
            comment.checkOwnerUser(user);
        }

        commentRepository.delete(comment);

        return "댓글이 삭제되었습니다.";
    }
}