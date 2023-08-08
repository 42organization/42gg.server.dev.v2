package com.gg.server.domain.megaphone.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class MegaphoneUseRequestDto {
    @NotNull
    private Long receiptId;
    @NotNull @Size(max = 30)
    private String content;
}
