package com.coldcicada.sequence.spring.starter.context;

import com.coldcicada.redisson.spring.starter.context.RedissonCommonConstant;
import com.coldcicada.sequence.spring.starter.property.Item;
import com.coldcicada.sequence.spring.starter.property.SequenceProperties;
import lombok.Getter;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author coldcicada
 * @Date 2024-06-26 14:00
 * @Description
 */
public class SequenceContext {

    private static final Logger log = LoggerFactory.getLogger(SequenceContext.class);

    @Getter
    public static ApplicationContext applicationContext;

    @Getter
    public static SequenceProperties sequenceProperties;

    @Getter
    public static final ConcurrentMap<String, Item> itemMap = new ConcurrentHashMap<>();

    @Getter
    public static final ConcurrentMap<String, ConcurrentLinkedQueue<String>> sequenceIndexMap = new ConcurrentHashMap<>();

    private SequenceContext() {}

    public static RedissonClient getRedissonClient() {

        try {

            String redissonClientBaneName = sequenceProperties.getRedisClient().contains(RedissonCommonConstant.CLIENT_BEAN_SUFFIX) ? sequenceProperties.getRedisClient() : sequenceProperties.getRedisClient() + RedissonCommonConstant.CLIENT_BEAN_SUFFIX;

            return applicationContext.getBean(redissonClientBaneName, RedissonClient.class);

        } catch (BeansException e) {

            log.error("获取RedissonClient异常", e);

            return null;
        }
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SequenceContext.applicationContext = applicationContext;
    }

    public static void setSequenceProperty(SequenceProperties sequenceProperties) {
        SequenceContext.sequenceProperties = sequenceProperties;
    }

}
