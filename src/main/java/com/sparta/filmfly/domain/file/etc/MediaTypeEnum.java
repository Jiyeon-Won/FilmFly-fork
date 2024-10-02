package com.sparta.filmfly.domain.file.etc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MediaTypeEnum {
    BOARD("boards/"),
    USER("profile-pictures/"),
    ETC("etc/");

    private final String s3FolderPrefix;
}