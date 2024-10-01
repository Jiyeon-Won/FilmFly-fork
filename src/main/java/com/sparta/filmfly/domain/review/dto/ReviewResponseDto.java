package com.sparta.filmfly.domain.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.filmfly.domain.review.entity.Review;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewResponseDto {

    private Long id;
    private Long userId;
    private Long movieId;
    private String movieTitle;
    private String nickname;
    private String pictureUrl;
    private Float rating;
    private String title;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private Long goodCount;
    private Long badCount;
    private Boolean isGood;
    private Boolean isBad;
    private Boolean isBlock;
    private Boolean isOwner;

    public ReviewResponseDto(Long id, Long userId, Long movieId, String movieTitle, String nickname, String pictureUrl, Float rating,
        String title, String content, LocalDateTime createdAt, Long goodCount, Long badCount) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.nickname = nickname;
        this.pictureUrl = pictureUrl;
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.goodCount = goodCount;
        this.badCount = badCount;
        this.isGood = false;
        this.isBad = false;
        this.isBlock = false;
        this.isOwner = false;
    }

    public ReviewResponseDto(
        Long id, Long movieId, String movieTitle, Float rating,
        String title, String content, LocalDateTime createdAt, Long goodCount, Long badCount
    ) {
        this.id = id;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.goodCount = goodCount;
        this.badCount = badCount;
    }

    public static ReviewResponseDto fromEntity(
        Review review, Long goodCount, Long badCount,
        Boolean isGood, Boolean isBad, Boolean isBlock, Boolean isOwner
    ) {
        return ReviewResponseDto.builder()
            .id(review.getId())
            .userId(review.getUser().getId())
            .nickname(review.getUser().getNickname())
            .pictureUrl(review.getUser().getPictureUrl())
            .rating(review.getRating())
            .title(review.getTitle())
            .content(review.getContent())
            .createdAt(review.getCreatedAt())
            .goodCount(goodCount)
            .badCount(badCount)
            .isGood(isGood)
            .isBad(isBad)
            .isBlock(isBlock)
            .isOwner(isOwner)
            .build();
    }

    public static ReviewResponseDto fromEntity(Review review, Long goodCount, Long badCount, Boolean isOwner) {
        return fromEntity(review, goodCount, badCount, false, false, false, isOwner);
    }

    public static ReviewResponseDto fromEntity(Review review) {
        return fromEntity(review, 0L, 0L, false, false, false, false);
    }

    public void setReactions(ReviewReactionCheckResponseDto dto) {
        if(dto.getIsGood() != null) this.isGood = dto.getIsGood();
        if(dto.getIsBad() != null) this.isBad = dto.getIsBad();
        if(dto.getIsBlock() != null) this.isBlock = dto.getIsBlock();
    }
}