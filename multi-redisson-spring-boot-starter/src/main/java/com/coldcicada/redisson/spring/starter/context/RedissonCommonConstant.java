package com.coldcicada.redisson.spring.starter.context;

/**
 * @Author coldcicada
 * @Date 2024-06-26 14:03
 * @Description
 */
public class RedissonCommonConstant {

    public static final String SYSTEM_NUMBER_CONFIG_KEY = "system-no";

    public static final String CLIENT_BEAN_SUFFIX = "RedissonClient";

    public static final String REDIS_KEY_SEPARATOR = RedissonContext.getApplicationContext().getEnvironment().getProperty("multi-redisson.redis-key-separator") == null ? ":" : RedissonContext.getApplicationContext().getEnvironment().getProperty("multi-redisson.redis-key-separator");

}
