package com.gg.server.domain.match;

import com.gg.server.domain.match.type.Option;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("matchUser")
@Getter
@NoArgsConstructor
public class RedisMatchUser {
    @Id
    private String id;
    private Integer ppp;
    private String option;//normal, both, rank

    public RedisMatchUser(String id, Integer ppp, Option option) {
        this.id = id;
        this.ppp = ppp;
        this.option = option.getCode();
    }
}
