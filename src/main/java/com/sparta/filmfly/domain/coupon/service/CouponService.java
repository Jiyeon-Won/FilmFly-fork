package com.sparta.filmfly.domain.coupon.service;

import com.sparta.filmfly.domain.coupon.dto.CouponPageResponseDto;
import com.sparta.filmfly.domain.coupon.dto.CouponRequestDto;
import com.sparta.filmfly.domain.coupon.dto.CouponResponseDto;
import com.sparta.filmfly.domain.coupon.entity.Coupon;
import com.sparta.filmfly.domain.coupon.entity.UserCoupon;
import com.sparta.filmfly.domain.coupon.repository.CouponRepository;
import com.sparta.filmfly.domain.coupon.repository.UserCouponRepository;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.detail.AccessDeniedException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private static final int COUPON_CODE_LENGTH = 8;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random random = new SecureRandom();

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    /**
     * 쿠폰 발급
     */
    @Transactional
    public List<CouponResponseDto> createCoupon(User user, CouponRequestDto requestDto) {

        validateExpirationDate(requestDto);

        List<CouponResponseDto> list = new ArrayList<>();

        // 임의 객체 쿠폰 만든 후, 관리자 유저 검증
        Coupon tmpCoupon = new Coupon("1234", requestDto);
        tmpCoupon.validUser(user);

        String title = "";
        for (int i = 0; i < requestDto.getQuantity(); i++) {
            // 랜덤 값(title) 중복되면 반복문 돌아감
            do {
                title = generateRandomCouponCode();
            } while (couponRepository.existsByTitle(title));
            Coupon coupon = Coupon.builder()
                    .title(title)
                    .requestDto(requestDto)
                    .build();

            couponRepository.save(coupon);
            list.add(CouponResponseDto.fromEntity(coupon));
        }

        return list;
    }

    /**
     * 쿠폰 전체 조회
     */
    @Transactional(readOnly = true)
    public List<CouponResponseDto> getAllCoupons() {
        List<CouponResponseDto> list = couponRepository.findAll()
                .stream()
                .map(CouponResponseDto::fromEntity)
                .toList();

        return list;
    }

    /**
     * 쿠폰 조회
     */
    @Transactional(readOnly = true)
    public CouponResponseDto getCoupon(Long id) {
        Coupon coupon = couponRepository.findByIdOrElseThrow(id);
        return CouponResponseDto.fromEntity(coupon);
    }

    /**
     * 쿠폰 삭제
     */
    @Transactional
    public CouponResponseDto deleteCoupon(User user, Long id) {

        Coupon coupon = couponRepository.findByIdOrElseThrow(id);

        coupon.validUser(user);

        couponRepository.delete(coupon);
        return CouponResponseDto.fromEntity(coupon);
    }

    /**
     * 유저의 발급받은 쿠폰 목록
     */
    public CouponPageResponseDto getUserCoupon(Long userId, Pageable pageable) {
        Page<Coupon> couponPage = userCouponRepository.findAllCouponsByUserId(userId, pageable);

        return CouponPageResponseDto.builder()
                .totalPages(couponPage.getTotalPages())
                .totalElements(couponPage.getTotalElements())
                .currentPage(couponPage.getNumber() + 1)
                .pageSize(couponPage.getSize())
                .content(couponPage.stream().map(CouponResponseDto::fromEntity).toList())
                .build();
    }

    /**
     * 유저 쿠폰 발급
     */
    @Transactional
    public CouponResponseDto distributeCoupons(User user, String description) {
        Coupon coupon;

        // 특정 description을 가진 쿠폰 중 발급되지 않은 쿠폰을 조회하고 예외 처리
        coupon = couponRepository.findTopByDescriptionAndIssuedFalseOrderByCreatedAtAscOrElseThrow(description);

        // 발급 완료되면 Issued = true
        coupon.updateIssuedTrue();

        UserCoupon userCoupon = UserCoupon.builder()
                .user(user)
                .coupon(coupon)
                .build();

        userCouponRepository.save(userCoupon);

        return CouponResponseDto.fromEntity(coupon);
    }

    /**
     * 쿠폰 기간 제대로 입력받았는지 검증하는 메서드
     */
    public void validateExpirationDate(CouponRequestDto requestDto) {
        // 오늘 날짜 ex) 2024 - 07 - 30 T 00:00:00
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)
                .withNano(0);
        if (requestDto.getExpirationDate().isBefore(todayStart)) {
            throw new AccessDeniedException(ResponseCodeEnum.COUPON_EXPIRATION_DATE_NOT_CORRECT);
        }
    }

    /**
     * 쿠폰 번호 랜덤 배정하는 메서드
     */
    public static String generateRandomCouponCode() {
        StringBuilder code = new StringBuilder(COUPON_CODE_LENGTH);
        for (int i = 0; i < COUPON_CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
}