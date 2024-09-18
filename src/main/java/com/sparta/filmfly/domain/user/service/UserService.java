package com.sparta.filmfly.domain.user.service;

import com.sparta.filmfly.domain.user.dto.UserDeleteRequestDto;
import com.sparta.filmfly.domain.user.dto.UserPasswordUpdateRequestDto;
import com.sparta.filmfly.domain.user.dto.UserProfileUpdateRequestDto;
import com.sparta.filmfly.domain.user.dto.UserResponseDto;
import com.sparta.filmfly.domain.user.dto.UserSearchPageResponseDto;
import com.sparta.filmfly.domain.user.dto.UserSignupRequestDto;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.domain.user.entity.UserRoleEnum;
import com.sparta.filmfly.domain.user.entity.UserStatusEnum;
import com.sparta.filmfly.domain.user.repository.UserRepository;
import com.sparta.filmfly.global.common.S3Uploader;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.detail.DuplicateException;
import com.sparta.filmfly.global.exception.custom.detail.InformationMismatchException;
import com.sparta.filmfly.global.exception.custom.detail.InvalidTargetException;
import com.sparta.filmfly.global.exception.custom.detail.UploadException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final S3Uploader s3Uploader;

    @Value("${admin_password}")
    private String managerPassword;

    /**
     * 회원가입
     */
    @Transactional
    public UserResponseDto signup(UserSignupRequestDto requestDto) {
        // 사용자 이름 중복 확인
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new DuplicateException(ResponseCodeEnum.USER_ALREADY_EXISTS);
        }

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new DuplicateException(ResponseCodeEnum.NICKNAME_ALREADY_EXISTS);
        }
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        UserRoleEnum userRole;
        if (requestDto.getAdminPassword() != null && !requestDto.getAdminPassword().isEmpty()) {
            if (!managerPassword.equals(requestDto.getAdminPassword())) {
                throw new InformationMismatchException(ResponseCodeEnum.INVALID_ADMIN_PASSWORD);
            }
            userRole = UserRoleEnum.ROLE_ADMIN;
        } else {
            // 이메일 인증 확인 (일반 유저의 경우)
            emailVerificationService.checkIfEmailVerified(requestDto.getEmail());
            userRole = UserRoleEnum.ROLE_USER;
        }

        User user = User.builder()
                .username(requestDto.getUsername())
                .password(encodedPassword)
                .email(requestDto.getEmail())
                .nickname(requestDto.getNickname())
                .userStatus(UserStatusEnum.ACTIVE)
                .userRole(userRole)
                .build();

        userRepository.save(user);

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userStatus(user.getUserStatus())
                .userRole(user.getUserRole())
                .build();
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void updatePassword(User loginUser, UserPasswordUpdateRequestDto requestDto) {
        User user = userRepository.findByIdOrElseThrow(loginUser.getId());

        user.validatePassword(requestDto.getCurrentPassword(), passwordEncoder);
        user.validateNewPassword(requestDto.getNewPassword(), passwordEncoder);

        String encodedNewPassword = passwordEncoder.encode(requestDto.getNewPassword());
        user.updatePassword(encodedNewPassword);
        userRepository.save(user);
    }

    /**
     * 프로필 업데이트
     */
    @Transactional
    public UserResponseDto updateProfile(User user, UserProfileUpdateRequestDto requestDto, MultipartFile profilePicture) {
        // 닉네임 중복 확인
        if (!user.getNickname().equals(requestDto.getNickname())) {
            checkNicknameDuplication(requestDto.getNickname());
        }

        String pictureUrl = user.getPictureUrl();

        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                if (!s3Uploader.isFileSame(profilePicture, pictureUrl)) {
                    if (pictureUrl != null && !pictureUrl.isEmpty()) {
                        s3Uploader.delete(pictureUrl); // 기존 프로필 사진 삭제
                    }
                    pictureUrl = s3Uploader.upload(profilePicture, "profile-pictures");
                }
            } catch (IOException e) {
                throw new UploadException(ResponseCodeEnum.UPLOAD_FAILED);
            }
        }

        user.updateProfile(requestDto.getNickname(), requestDto.getIntroduce(), pictureUrl);
        userRepository.save(user);

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .introduce(user.getIntroduce())
                .pictureUrl(user.getPictureUrl())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * 닉네임 중복 조회
     */
    @Transactional(readOnly = true)
    public void checkNicknameDuplication(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicateException(ResponseCodeEnum.NICKNAME_ALREADY_EXISTS);
        }
    }

    /**
     * 프로필 조회
     */
    @Transactional(readOnly = true)
    public UserResponseDto getProfile(Long userId) {
        User user = userRepository.findByIdOrElseThrow(userId);
        return UserResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .introduce(user.getIntroduce())
                .pictureUrl(user.getPictureUrl())
                .build();
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteUser(User user, UserDeleteRequestDto requestDto) {
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new InformationMismatchException(ResponseCodeEnum.PASSWORD_INCORRECT);
        }

        user.deleteRefreshToken();
        user.updateDeleted();
        userRepository.save(user);
    }

    /**
     * 유저 본인 탈퇴 계정 복구
     */
    @Transactional
    public UserResponseDto activateUser(User user) {
        // 활성화 상태 검증
        user.validateActiveStatus();
        user.updateVerified();
        userRepository.save(user);

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .userStatus(user.getUserStatus())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * 유저 상세 조회(관리자 기능)
     */
    @Transactional(readOnly = true)
    public UserResponseDto getUserDetail(Long userId) {
        User user = userRepository.findByIdOrElseThrow(userId);
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .introduce(user.getIntroduce())
                .pictureUrl(user.getPictureUrl())
                .userRole(user.getUserRole())
                .userStatus(user.getUserStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }

    /**
     * 유저 검색 조회(관리자 기능)
     */
    @Transactional(readOnly = true)
    public UserSearchPageResponseDto getUsersBySearch(
        String search, UserStatusEnum status, int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage;

        if (search != null && !search.isEmpty() && status != null) {
            usersPage = userRepository.findBySearchAndStatus(search, status, pageable);
        } else if (search != null && !search.isEmpty()) {
            usersPage = userRepository.findByUsernameOrNicknameContaining(search, pageable);
        } else if (status != null) {
            usersPage = userRepository.findAllByUserStatus(status, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }

        List<UserResponseDto> userResponseDtos = usersPage.getContent().stream()
                .map(user -> UserResponseDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .userRole(user.getUserRole())
                        .build())
                .collect(Collectors.toList());

        return UserSearchPageResponseDto.builder()
                .users(userResponseDtos)
                .totalElements(usersPage.getTotalElements())
                .currentPage(usersPage.getNumber() + 1)
                .totalPages(usersPage.getTotalPages())
                .pageSize(size)
                .build();
    }

    /**
     * 유저 정지 시키기(관리자 기능)
     */
    @Transactional
    public UserResponseDto suspendUser(Long userId) {
        User user = userRepository.findByIdOrElseThrow(userId);

        if (user.getUserRole() != UserRoleEnum.ROLE_USER) {
            throw new InvalidTargetException(ResponseCodeEnum.INVALID_ADMIN_TARGET);
        }

        user.validateDeletedStatus();
        user.validateSuspendedStatus();

        user.updateSuspended();
        userRepository.save(user);

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .userStatus(user.getUserStatus())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * 유저 활성화 시키기(관리자 기능)
     */
    @Transactional
    public UserResponseDto activateUserAsAdmin(Long userId) {
        User user = userRepository.findByIdOrElseThrow(userId);
        user.validateActiveStatus();

        user.updateVerified();
        userRepository.save(user);

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .userStatus(user.getUserStatus())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * 마이페이지 front 연동 위한 Service
     */
    @Transactional(readOnly = true)
    public UserResponseDto getMyUserInfo(User user) {
        return UserResponseDto.builder()
            .id(user.getId())
            .nickname(user.getNickname())
            .introduce(user.getIntroduce())
            .pictureUrl(user.getPictureUrl())
            .deletedAt(user.getDeletedAt())
            .build();
    }
    /**
     * 유저 수 반환
     */
    public long getUserCount() {
        return userRepository.count();
    }
}