package com.gg.server.domain.match.data;

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
    private String nickName;
    private Integer ppp;
    private String option;//normal, both, rank

    public RedisMatchUser(String nickName, Integer ppp, Option option) {
        this.nickName = nickName;
        this.ppp = ppp;
        this.option = option.getCode();
    }
}
