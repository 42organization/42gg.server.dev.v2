package com.gg.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
public class UserModifyRequestDto {
        @Pattern(regexp = "(PENHOLDER|SHAKEHAND|DUAL|NONE)", message = "Invalid value for field 'value'")
        private String racketType;
        private String statusMessage;
        @Pattern(regexp = "(SLACK|NONE|EMAIL|BOTH)", message = "Invalid value for field 'value'")
        private String snsNotiOpt;
}
