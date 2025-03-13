package com.movienetscape.resourceserver.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenRevocationService {

    private final RedisTemplate<String, String> redisTemplate;

    public TokenRevocationService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isTokenRevoked(String jti) {

        return redisTemplate.opsForValue().get(jti) != null;
    }
}

