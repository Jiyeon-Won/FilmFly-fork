package com.sparta.filmfly.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserKakaoInfoDto {
    private Long id;
    private String nickname;
    private String pictureUrl;
    private String email;
}
