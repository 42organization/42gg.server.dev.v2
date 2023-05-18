package com.gg.server.domain.noti.service.sns;

import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.user.dto.UserDto;

public interface SnsSender {
    void send(UserDto user, Noti noti);
}
