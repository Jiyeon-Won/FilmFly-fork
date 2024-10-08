package com.sparta.filmfly.domain.report.dto;

import com.sparta.filmfly.domain.report.entity.ReportTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReportRequestDto {
    @NotNull
    private Long reportedId;

    @NotNull
    private Long typeId;

    @NotNull
    private ReportTypeEnum type;

    @NotNull
    private String reason;
}
