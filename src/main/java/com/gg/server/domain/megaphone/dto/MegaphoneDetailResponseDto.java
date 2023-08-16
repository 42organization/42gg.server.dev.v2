package com.gg.server.domain.megaphone.dto;

import com.gg.server.domain.megaphone.data.Megaphone;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MegaphoneDetailResponseDto {
    private Long megaphoneId;
    private String content;
    private String usedAt;

    public MegaphoneDetailResponseDto(Megaphone megaphone) {
        this.megaphoneId = megaphone.getId();
        this.content = megaphone.getContent();
        this.usedAt = megaphone.getUsedAt().toString();
    }
}
