package com.sparta.filmfly.global.common.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CursorResponseDto<T> {
    private Long lastFetchedId;
    private boolean hasNext;
    private List<T> data;

    public static <T> CursorResponseDto<T> of(List<T> data, Long lastFetchedId, boolean hasNext) {
        return CursorResponseDto.<T>builder()
            .lastFetchedId(lastFetchedId)
            .hasNext(hasNext)
            .data(data)
            .build();
    }
}