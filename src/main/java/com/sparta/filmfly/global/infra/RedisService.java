package com.sparta.filmfly.global.infra;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis에 저장. 같은 key일 경우 덮어쓰기가 됨
     */
    public <T> void set(String key, T value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? clazz.cast(value) : null;
    }

    /**
     * 만료시간은 유지한 채 값 변경
     */
    public <T> void update(String key, T value) {
        if (this.hasKey(key)) {
            Long expire = redisTemplate.getExpire(key);

            redisTemplate.opsForValue().set(key, value);

            if (expire != null && expire > 0) {
                this.setExpiry(key, expire, TimeUnit.SECONDS);
            }
        }
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setExpiry(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }
}