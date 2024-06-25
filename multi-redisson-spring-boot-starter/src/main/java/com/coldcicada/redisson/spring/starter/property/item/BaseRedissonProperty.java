package com.coldcicada.redisson.spring.starter.property.item;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author coldcicada
 * @Date 2024-06-25 16:58
 * @Description
 */
@Getter
@Setter
@Accessors(chain = true)
public class BaseRedissonProperty {

    /**
     * 连接空闲超时，单位：毫秒 默认值 10000
     */
    protected int idleConnectionTimeout = 10000;

    /**
     * 连接超时，单位：毫秒 默认值 10000
     */
    protected int connectTimeout = 10000;

    /**
     * 命令等待超时，单位：毫秒 默认值 3000
     */
    protected int timeout = 3000;

    /**
     * 命令失败重试次数 默认值 3
     */
    protected int retryAttempts = 3;

    /**
     * 命令重试发送时间间隔，单位：毫秒 默认值 1500
     */
    protected int retryInterval = 1500;

    /**
     * 密码
     */
    protected String password;

    /**
     * 用户名
     */
    protected String username;

    /**
     * 客户端名称
     */
    protected String clientName;

    /**
     * 编码 默认值 org.redisson.codec.JsonJacksonCodec
     */
    protected String codec = "org.redisson.codec.JsonJacksonCodec";

    /**
     * 使用的redis数据库索引
     */
    protected int database;

    /**
     * 线程池数量 这个线程池数量被所有RTopic对象监听器，RRemoteService调用者和RExecutorService任务共同共享。
     */
    protected int threads ;

    /**
     * Netty线程池数量 默认值: 当前处理核数量 * 2 这个线程池数量是在一个Redisson实例内，被其创建的所有分布式数据类型和服务，以及底层客户端所一同共享的线程池里保存的线程数量。
     */
    protected int nettyThreads;

    /**
     * Redis 订阅连接的最小闲置链接
     */
    private int subscriptionConnectionMinimumIdleSize = 1;

    /**
     * Redis 订阅连接的最大池大小
     */
    private int subscriptionConnectionPoolSize = 50;

}
