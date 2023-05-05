package com.gg.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserModifyRequestDto {
        private String racketType;
        private String statusMessage;
        private String snsNotiOpt;
}
