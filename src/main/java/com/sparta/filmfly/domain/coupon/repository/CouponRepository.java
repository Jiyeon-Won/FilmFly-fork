package com.sparta.filmfly.domain.coupon.repository;

import com.sparta.filmfly.domain.coupon.entity.Coupon;
import com.sparta.filmfly.global.common.batch.hardDelete.SoftDeletableRepository;
import com.sparta.filmfly.global.common.response.PageResponseDto;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.detail.NotFoundException;
import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long>, SoftDeletableRepository<Coupon> {
    default Coupon findByIdOrElseThrow(Long couponId) {
        return findById(couponId)
                .orElseThrow(() -> new NotFoundException(ResponseCodeEnum.COUPON_NOT_FOUND));
    }

    Boolean existsByTitle(String title);
    Optional<Coupon> findTopByIssuedFalseOrderByCreatedAtAsc();
    // Status가 True(사용가능한 쿠폰) 이면서, 만료기간 오름차순으로 정렬
    List<Coupon> findAllByStatusTrueOrderByExpirationDateAsc();

    Optional<Coupon> findTopByDescriptionAndIssuedFalseOrderByCreatedAtAsc(String description);

    default Coupon findTopByDescriptionAndIssuedFalseOrderByCreatedAtAscOrElseThrow(String description) {
        return findTopByDescriptionAndIssuedFalseOrderByCreatedAtAsc(description)
                .orElseThrow(() -> new NotFoundException(ResponseCodeEnum.COUPON_NOT_FOUND));
    }
}