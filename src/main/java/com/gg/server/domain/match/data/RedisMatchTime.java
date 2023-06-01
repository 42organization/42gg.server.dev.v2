package com.gg.server.domain.match.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.gg.server.domain.match.type.Option;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("matchTime")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RedisMatchTime implements Serializable {
    @Id
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startTime;
    private Option option;

    public RedisMatchTime(LocalDateTime startTime, Option option) {
        this.startTime = startTime;
        this.option = option;
    }
}
