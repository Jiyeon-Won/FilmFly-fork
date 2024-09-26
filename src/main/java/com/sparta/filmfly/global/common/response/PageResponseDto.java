package com.sparta.filmfly.global.common.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageImpl;

@Getter
@Builder
public class PageResponseDto<T> {
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private List<T> data;

    public static <T> PageResponseDto<T> of(PageImpl<T> page) {
        return PageResponseDto.<T>builder()
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .currentPage(page.getNumber() + 1)
            .pageSize(page.getSize())
            .data(page.getContent())
            .build();
    }
}