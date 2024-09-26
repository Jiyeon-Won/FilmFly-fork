package com.sparta.filmfly.domain.board.service;

import com.sparta.filmfly.domain.board.dto.*;
import com.sparta.filmfly.domain.board.entity.Board;
import com.sparta.filmfly.domain.board.repository.BoardRepository;
import com.sparta.filmfly.domain.file.service.FileService;
import com.sparta.filmfly.domain.media.entity.MediaTypeEnum;
import com.sparta.filmfly.domain.reaction.ReactionContentTypeEnum;
import com.sparta.filmfly.domain.reaction.dto.ReactionCheckResponseDto;
import com.sparta.filmfly.domain.reaction.service.BadService;
import com.sparta.filmfly.domain.reaction.service.GoodService;
import com.sparta.filmfly.domain.review.dto.ReviewUserResponseDto;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.domain.user.entity.UserRoleEnum;
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
public class BoardService {

    private final BoardRepository boardRepository;
    private final GoodService goodService;
    private final BadService badService;
    private final FileService fileService;
    /**
     * 보드 생성
     */
    @Transactional
    public BoardResponseDto createBoard(User user, BoardRequestDto requestDto) {
        Board entity = requestDto.toEntity(user);
        Board savedBoard = boardRepository.save(entity); // 일단 저장 후 boardId를 생성

        //이미지 올릴때 검사, 이미지 전부 합쳐서 20MB 못넘게 하기 필요할까??
        String content = requestDto.getContent();
        String modifiedContent = fileService.uploadLocalImageToS3(MediaTypeEnum.BOARD, savedBoard.getId(), content);

        savedBoard.updateContent(null, modifiedContent);
        Board updatedBoard = boardRepository.save(savedBoard);

        return BoardResponseDto.fromEntity(updatedBoard,0L,0L);
    }

    /**
     * 보드 조회
     */
    @Transactional
    public BoardReactionResponseDto getBoard(UserDetailsImpl userDetails, Long boardId) {
        Board findBoard = boardRepository.findByIdOrElseThrow(boardId);
        findBoard.addHits();

        BoardResponseDto board = boardRepository.getBoard(boardId);
        ReactionCheckResponseDto reactions = ReactionCheckResponseDto.setupFalse();
        boolean isOwner = false;
        if (userDetails != null) {
            reactions = boardRepository.checkBoardReaction(userDetails.getUser(), boardId);

            if (Objects.equals(board.getUserId(), userDetails.getUser().getId())) {
                isOwner = true;
            }
        }

        return BoardReactionResponseDto.of(board, reactions, isOwner);
    }

    /**
     * 보드 페이징 조회
     */
    public PageResponseDto<BoardPageDto> getPageBoard(Long filterGoodCount, Long filterHits, String search, Pageable pageable) {
        return boardRepository.findAllWithFilters(pageable, filterGoodCount, filterHits, search);
    }

    /**
     * 유저의 보드 조회
     */
    public PageResponseDto<BoardPageDto> getUsersBoard(UserDetailsImpl userDetails, Long userId, Pageable pageable) {
        PageResponseDto<BoardPageDto> pageBoard = boardRepository.findAllByUserId(userId, pageable);

        if (userDetails != null) {
            List<BoardPageDto> boards = pageBoard.getData();
            for (BoardPageDto board : boards) {
                if (Objects.equals(userDetails.getUser().getId(), board.getUserId())) {
                    board.setOwner(true);
                }
            }
        }

        return pageBoard;
    }

    /**
     * 보드 수정 권한 체크
     */
    public Boolean getBoardUpdatePermission(User user, Long boardId) {
        Board board = boardRepository.findByIdOrElseThrow(boardId);
        //admin이면 true 반환
        if(!user.isAdmin()) {
            board.checkOwnerUser(user);
        }
        return true; //수정 권한 없으면 에러?
    }

    /**
     * 보드 수정 페이지 정보
     */
    public BoardUpdateResponseDto forUpdateBoard(User user, Long boardId) {
        Board board = boardRepository.findByIdOrElseThrow(boardId);
        board.checkOwnerUser(user);
        return BoardUpdateResponseDto.fromEntity(board);
    }

    /**
     * 보드 수정
     */
    @Transactional
    public BoardResponseDto updateBoard(User user, BoardRequestDto requestDto, Long boardId) {
        Board board = boardRepository.findByIdOrElseThrow(boardId);
        board.checkOwnerUser(user);

        String content = requestDto.getContent();
        fileService.checkModifiedImageFile(MediaTypeEnum.BOARD, board.getId(), content); //이미지 변경 확인
        String modifiedContent = fileService.uploadLocalImageToS3(MediaTypeEnum.BOARD,board.getId(),content); //이미지 S3 변환

        board.updateContent(requestDto.getTitle(),modifiedContent);
        Board updatedBoard = boardRepository.save(board);

        Long goodCount = goodService.getCountByTypeTypeId(ReactionContentTypeEnum.BOARD,boardId);
        Long badCount = badService.getCountByTypeTypeId(ReactionContentTypeEnum.BOARD,boardId);
        return BoardResponseDto.fromEntity(updatedBoard,goodCount,badCount);
    }

    /**
     * 보드 삭제
     */
    @Transactional
    public String deleteBoard(User user, Long boardId) {
        Board board = boardRepository.findByIdOrElseThrow(boardId);

        //관리자면 삭제 가능하게
        if(user.getUserRole() == UserRoleEnum.ROLE_USER) {
            board.checkOwnerUser(user);
        }

        boardRepository.delete(board);

        return "게시물이 삭제되었습니다.";
    }

    /**
     * 보드 수 반환
     */
    public long getBoardCount() {
        return boardRepository.count();
    }
}