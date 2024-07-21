package com.sparta.filmfly.domain.officeboard.service;

import static com.sparta.filmfly.domain.user.entity.UserRoleEnum.ROLE_ADMIN;

import com.sparta.filmfly.domain.officeboard.dto.OfficeBoardRequestDto;
import com.sparta.filmfly.domain.officeboard.dto.OfficeBoardResponseDto;
import com.sparta.filmfly.domain.officeboard.entity.OfficeBoard;
import com.sparta.filmfly.domain.officeboard.repository.OfficeBoardRepository;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.domain.user.repository.UserRepository;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.detail.NotFoundException;
import com.sparta.filmfly.global.exception.custom.detail.UnAuthorizedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
// 공통 -> 로그인 완료되면 유저 추가
public class OfficeBoardService {

    private final OfficeBoardRepository officeBoardRepository;
    private final UserRepository userRepository;


    @Transactional
    public OfficeBoardResponseDto createOfficeBoard(User user, OfficeBoardRequestDto requestDto) {
        // validUser(user);
        OfficeBoard officeBoard = OfficeBoard.builder()
                .user(user)
                .requestDto(requestDto)
                .build();

        officeBoardRepository.save(officeBoard);
        return OfficeBoardResponseDto.fromEntity(officeBoard);
    }

    @Transactional
    public List<OfficeBoardResponseDto> findAll(Pageable pageable) {

        List<OfficeBoardResponseDto> list = officeBoardRepository.findAll(pageable)
                .stream()
                .map(OfficeBoardResponseDto::fromEntity)
                .toList();

        return list;
    }

    @Transactional
    public OfficeBoardResponseDto findOne(Long id) {
        // User user
        OfficeBoard officeBoard = officeBoardRepository.findById(id).orElseThrow(
                () -> new NotFoundException(ResponseCodeEnum.BOARD_NOT_FOUND)
        );

        return OfficeBoardResponseDto.fromEntity(officeBoard);

    }

    @Transactional
    public OfficeBoardResponseDto update(Long id, OfficeBoardRequestDto requestDto) {
        //User user
        // 사용자만 수정 할 수 있습니다.

        OfficeBoard officeBoard = officeBoardRepository.findById(id).orElseThrow(
                () -> new NotFoundException(ResponseCodeEnum.BOARD_NOT_FOUND)
        );

        officeBoard.update(requestDto);
        return OfficeBoardResponseDto.fromEntity(officeBoard);

    }

    @Transactional
    public OfficeBoardResponseDto delete(Long id) {
        //User user
        //사용자만 삭제 할 수 있습니다.

        OfficeBoard officeBoard = officeBoardRepository.findById(id).orElseThrow(
                () -> new NotFoundException(ResponseCodeEnum.BOARD_NOT_FOUND)
        );

        officeBoard.delete();
        return OfficeBoardResponseDto.fromEntity(officeBoard);
    }

    public void validUser(User user) {
        if (user.getUserRole() != ROLE_ADMIN) {
            throw new UnAuthorizedException(ResponseCodeEnum.USER_UNAUTHORIZED);
        }
    }

}