package com.coldcicada.redisson.spring.starter.property.item;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author coldcicada
 * @Date 2024-03-18 14:32
 * @Description
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SingleRedissonProperty extends BaseRedissonProperty{

    /**
     * redis 服务器地址
     */
    private String address;

    /**
     * 最小闲置 Redis 连接数
     */
    private int connectionMinimumIdleSize = 24;

    /**
     * Redis 连接池最大容量
     */
    private int connectionPoolSize = 64;

    /**
     * 以毫秒为单位的 DNS 检查时间间隔
     */
    private long dnsMonitoringInterval = 5000;

}
