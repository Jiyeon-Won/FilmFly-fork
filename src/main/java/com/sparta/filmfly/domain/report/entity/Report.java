package com.sparta.filmfly.domain.report.entity;

import com.sparta.filmfly.domain.user.entity.User;
import com.sparta.filmfly.global.common.TimeStampEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends TimeStampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporterId; // 신고자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id", nullable = false)
    private User reportedId; // 신고된 사용자

    @Column(nullable = false)
    private String content; // 신고된 콘텐츠의 원본 내용

    @Column(nullable = false)
    private Long typeId; // 게시물, 댓글, 리뷰의 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportTypeEnum type; // 게시물, 댓글, 리뷰 타입

    @Column(nullable = false)
    private String reason; // 신고 이유
}