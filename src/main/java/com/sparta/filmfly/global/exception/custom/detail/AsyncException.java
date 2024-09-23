package com.sparta.filmfly.global.exception.custom.detail;

import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.GlobalException;

public class AsyncException extends GlobalException {
    public AsyncException(ResponseCodeEnum examCodeEnum) {
        super(examCodeEnum);
    }
}