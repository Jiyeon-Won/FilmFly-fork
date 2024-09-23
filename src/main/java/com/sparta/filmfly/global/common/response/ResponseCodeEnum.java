package com.sparta.filmfly.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseCodeEnum {

    // Common
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),

    // Auth
    INVALID_REQUEST(HttpStatus.NOT_FOUND, "해당 요청을 처리 할 수 없습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인 실패"),
    INVALID_TOKENS(HttpStatus.BAD_REQUEST, "유효하지 않는 토큰입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    USER_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "사용자는 이미 인증되었습니다."),
    USER_ACTIVE(HttpStatus.BAD_REQUEST, "현재 활성화 상태입니다."),
    USER_DELETED(HttpStatus.BAD_REQUEST, "현재 탈퇴한 사용자입니다."),
    USER_SUSPENDED(HttpStatus.FORBIDDEN, "현재 정지된 상태입니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "이메일을 찾을 수 없습니다."),
    INVALID_ADMIN_PASSWORD(HttpStatus.FORBIDDEN, "관리자 비밀번호가 일치하지 않습니다."),
    INVALID_ADMIN_TARGET(HttpStatus.FORBIDDEN, "관리자를 대상으로 할 수 없습니다."),
    INVALID_SELF_TARGET(HttpStatus.BAD_REQUEST, "본인을 대상으로 할 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    SAME_AS_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "새 비밀번호가 현재 비밀번호와 동일합니다."),
    EMAIL_VERIFICATION_REQUIRED(HttpStatus.FORBIDDEN, "이메일 인증이 필요합니다."),
    EMAIL_VERIFICATION_EXPIRED(HttpStatus.BAD_REQUEST, "이메일 인증 코드가 만료되었습니다."),
    EMAIL_RESEND_LIMIT(HttpStatus.BAD_REQUEST, "한 시간내에 5회 까지만 요청이 가능합니다"),
    EMAIL_VERIFICATION_TOKEN_MISMATCH(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    EMAIL_VERIFICATION_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 인증번호 전송에 실패했습니다."),

    // Report
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "신고내용을 찾을 수 없습니다."),
    ALREADY_REPORTED(HttpStatus.BAD_REQUEST, "이미 신고 처리 되었습니다."),

    //Block
    USER_ALREADY_BLOCKED(HttpStatus.BAD_REQUEST, "이미 차단된 사용자입니다."),
    NOT_BLOCKED_TARGET(HttpStatus.NOT_FOUND, "차단된 상대가 아닙니다."),

    // Favorite
    FAVORITE_ALREADY_EXISTS(HttpStatus.CONFLICT, "찜이 이미 등록되어있습니다."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "찜을 하지 않은 영화입니다."),


    // Board
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "보드를 찾을 수 없습니다."),
    BOARD_NOT_OWNER(HttpStatus.FORBIDDEN, "보드의 주인이 아닙니다."),
    BOARD_FILE_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "보드 파일 처리 중 오류가 발생했습니다."),
    
    //Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COMMENT_NOT_OWNER(HttpStatus.FORBIDDEN, "댓글의 주인이 아닙니다."),
    COMMENT_PATH_MISMATCH(HttpStatus.BAD_REQUEST, "댓글의 보드 경로가 일치하지 않습니다."),
    // Review
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    REVIEW_NOT_OWNER(HttpStatus.FORBIDDEN, "본인이 작성한 리뷰가 아닙니다."),

    // REACTION
    REACTION_CONTENT_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "컨텐츠 타입을 찾을 수 없습니다."),
    GOOD_ALREADY_ADD(HttpStatus.CONFLICT, "이미 좋아요를 등록했습니다."),
    GOOD_ALREADY_REMOVE(HttpStatus.CONFLICT, "이미 좋아요를 취소했습니다."),
    BAD_ALREADY_ADD(HttpStatus.CONFLICT, "이미 싫어요를 등록했습니다."),
    BAD_ALREADY_REMOVE(HttpStatus.CONFLICT, "이미 싫어요를 취소했습니다."),

    // Collection
    COLLECTION_ALREADY_EXISTS(HttpStatus.ALREADY_REPORTED, "같은 이름의 보관함이 이미 있습니다."),
    COLLECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "보관함을 찾을 수 없습니다."),
    COLLECTION_NOT_OWNER(HttpStatus.FORBIDDEN, "보관함의 주인이 아닙니다."),

    // MovieCollection
    MOVIE_COLLECTION_ALREADY_EXISTS(HttpStatus.ALREADY_REPORTED, "이미 보관함에 등록된 영화입니다."),
    MOVIE_COLLECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "보관함에서 해당 영화를 찾을 수 없습니다."),

    // Movie
    MOVIE_NOT_FOUND(HttpStatus.NOT_FOUND, "영화를 찾을 수 없습니다."),
    API_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "API 요청에 실패했습니다."),
    // Credit
    CREDIT_NOT_FOUND(HttpStatus.NOT_FOUND, "배우를 찾을 수 없습니다."),

    // Coupon
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 쿠폰을 찾을 수 없습니다"),
    COUPON_EXHAUSTED(HttpStatus.NOT_FOUND, "이벤트 쿠폰이 모두 소진되었습니다."),
    COUPON_EXPIRATION_DATE_NOT_CORRECT(HttpStatus.FORBIDDEN, "만료일은 오늘 이후여야 합니다."),

    ;
    private final HttpStatus httpStatus;
    private final String message;
}