package com.gg.server.domain.noti.service.sns;

import com.gg.server.domain.noti.data.Noti;
import com.gg.server.domain.noti.dto.UserNotiDto;

public interface SnsSender {
    void send(UserNotiDto user, Noti noti);
}
