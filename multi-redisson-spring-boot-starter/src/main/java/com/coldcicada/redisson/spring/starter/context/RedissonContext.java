package com.coldcicada.redisson.spring.starter.context;

import lombok.Getter;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author coldcicada
 * @Date 2024-06-25 17:01
 * @Description
 */
public class RedissonContext {

    private @Getter static ApplicationContext applicationContext;

    private @Getter static final ConcurrentMap<String, RedissonClient> redissonClientMap = new ConcurrentHashMap<>();

    private @Getter static final Map<String, String> redisScriptShaMap = new HashMap<>();

    /**
     * @Description: 用于存储spring上下文
     * @param applicationContext
     * @Return void
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        RedissonContext.applicationContext = applicationContext;
    }

    /**
     * @Description: 判断lua脚本SH1是否存在
     * @param scriptName
     * @Return the redisScriptShaMap whether contains script name
     * @Author: coldcicada
     * @Date: 2024/3/18
     */
    public static boolean containsLua(String scriptName) {
        return redisScriptShaMap.containsKey(scriptName);
    }

    /**
     * @Description: 获取相应脚本的SH1数值
     * @param scriptName
     * @Return java.lang.String
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static String getLuaSha(String scriptName) {
        return redisScriptShaMap.getOrDefault(scriptName, null);
    }

    /**
     * @Description: 获取redisson客户端
     * @param clientBeanName
     * @Return org.redisson.api.RedissonClient
     * @Author: coldcicada
     * @Date: 2024/6/25
     */
    public static RedissonClient getRedissonClient(String clientBeanName) {
        return redissonClientMap.get(clientBeanName);
    }

    private RedissonContext() {
    }

}
