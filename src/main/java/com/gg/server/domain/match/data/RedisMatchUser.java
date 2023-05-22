package com.gg.server.domain.match.data;

import com.gg.server.domain.match.type.Option;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RedisMatchUser {
    @Id
    private Long userId;
    private Integer ppp;
    private String option;//normal, both, rank

    public RedisMatchUser(Long userId, Integer ppp, Option option) {
        this.userId = userId;
        this.ppp = ppp;
        this.option = option.getCode();
    }
}
