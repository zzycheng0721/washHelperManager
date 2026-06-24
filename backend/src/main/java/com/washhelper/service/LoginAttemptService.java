package com.washhelper.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class LoginAttemptService {

    public static final int MAX_FAILURES = 5;
    private static final Duration FAIL_WINDOW = Duration.ofMinutes(5);
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private static final String FAIL_PREFIX = "washhelper:login:fail:";
    private static final String LOCK_PREFIX = "washhelper:login:lock:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public long lockedSeconds(String identity) {
        Long ttl = redisTemplate.getExpire(LOCK_PREFIX + identity);
        return ttl == null || ttl < 0 ? 0L : ttl;
    }

    public int recordFailure(String identity) {
        String key = FAIL_PREFIX + identity;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, FAIL_WINDOW);
        }
        int current = count == null ? 0 : count.intValue();
        if (current >= MAX_FAILURES) {
            redisTemplate.opsForValue().set(LOCK_PREFIX + identity, String.valueOf(System.currentTimeMillis()), LOCK_DURATION);
            redisTemplate.delete(key);
        }
        return current;
    }

    public void reset(String identity) {
        redisTemplate.delete(FAIL_PREFIX + identity);
        redisTemplate.delete(LOCK_PREFIX + identity);
    }

    public int remaining(String identity) {
        String count = redisTemplate.opsForValue().get(FAIL_PREFIX + identity);
        int used = count == null ? 0 : Integer.parseInt(count);
        return Math.max(0, MAX_FAILURES - used);
    }
}
