package com.example.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class FlowUtils {
    @Resource
    StringRedisTemplate template;

    private static final LimitAction defaultAction = overclock -> !overclock;

    /**
     * @param key
     * @param blockTime
     * @return
     */
    public boolean limitOnceCheck(String key, int blockTime) {
        if (Boolean.TRUE.equals(template.hasKey(key))) {
            return false;
        } else {
            template.opsForValue().set(key, "", blockTime, TimeUnit.SECONDS);
            return true;
        }
    }

    /**
     * 内部使用，限制行为与策略
     */
    private interface LimitAction {
        boolean run(boolean overclock);
    }

    /**
     * 内部使用请求限制主要逻辑
     *
     * @param key       计数键
     * @param frequency 请求频率
     * @param period    计数周期
     * @param action    限制行为与策略
     * @return 是否通过限流检查
     */
    private boolean internalCheck(String key, int frequency, int period, LimitAction action) {
        String count = template.opsForValue().get(key);
        if (count != null) {
            long value = Optional.ofNullable(template.opsForValue().increment(key)).orElse(0L);
            int c = Integer.parseInt(count);
            if (value != c + 1)
                template.expire(key, period, TimeUnit.SECONDS);
            return action.run(value > frequency);
        } else {
            template.opsForValue().set(key, "1", period, TimeUnit.SECONDS);
            return true;
        }
    }


    /**
     * 针对于在时间段内多次请求限制,如3秒内20次请求
     *
     * @param counterKey 计数键
     * @param frequency  请求频率
     * @param period     技术周期
     * @return 是否通过限流检查
     */
    public boolean limitPeriodCounterCheck(String counterKey, int frequency, int period) {
        return this.internalCheck(counterKey, frequency, period, defaultAction);
    }


}
