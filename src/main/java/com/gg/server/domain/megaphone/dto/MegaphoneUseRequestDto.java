package com.gg.server.domain.megaphone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MegaphoneUseRequestDto {
    @NotNull
    private Long receiptId;
    @NotNull @Size(max = 30)
    private String content;
}
