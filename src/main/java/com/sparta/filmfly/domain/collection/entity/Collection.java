package com.sparta.filmfly.domain.collection.entity;

import com.sparta.filmfly.domain.collection.dto.CollectionRequestDto;
import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.common.TimeStampEntity;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.detail.AccessDeniedException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Collection extends TimeStampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    private String content;

    @Builder
    public Collection(User user, String name, String content) {
        this.user = user;
        this.name = name;
        this.content = content;
    }

    public void updateCollection(CollectionRequestDto requestDto) {
        if (requestDto.getName() != null) this.name = requestDto.getName();
        if (requestDto.getContent() != null) this.content = requestDto.getContent();
    }

    /**
     * 요청한 유저가 소유주인지 확인
     */
    public void validateOwner(User requestUser) {
        if(!Objects.equals(this.user.getId(), requestUser.getId()))
            throw new AccessDeniedException(ResponseCodeEnum.COLLECTION_NOT_OWNER);
    }
}