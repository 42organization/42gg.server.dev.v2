package com.gg.server.domain.user.dto;

import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.SnsType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserModifyRequestDto {

        private RacketType racketType;
        private String statusMessage;
        private SnsType snsNotiOpt;
}
