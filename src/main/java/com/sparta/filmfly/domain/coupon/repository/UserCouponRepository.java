package com.sparta.filmfly.domain.coupon.repository;

import com.sparta.filmfly.domain.coupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

}
