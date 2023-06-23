package com.gg.server.domain.game.keyGenerator;

import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

public class GameKeyGenerator implements KeyGenerator {
    private static final String KEY_FORMAT = "game:all:";

    @Override
    public Object generate(Object target, Method method, Object... params) {
        
        return null;
    }
}
