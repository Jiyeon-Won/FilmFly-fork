package com.sparta.filmfly.domain.coupon.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CouponPageResponseDto {
    Long totalElements;
    int totalPages;
    int currentPage;
    int pageSize;
    List<CouponResponseDto> content;
}
