package com.coldcicada.redisson.spring.starter.property.item;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author coldcicada
 * @Date 2024-03-18 14:48
 * @Description
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ClusterRedissonProperty extends BaseRedissonProperty{

    /**
     * 重新连接时间间隔，单位：毫秒 默认值 3000
     */
    private int failedSlaveReconnectionInterval = 3000;

    /**
     * 单个连接最大订阅数量
     */
    private int subscriptionsPerConnection = 5;

    /**
     * 负载均衡算法类的选择 默认值： `org.redisson.connection.balancer.RoundRobinLoadBalancer`
     * 在多Redis服务节点的环境里，可以选用以下几种负载均衡方式选择一个节点：
     * `org.redisson.connection.balancer.WeightedRoundRobinBalancer` - 权重轮询调度算法
     * `org.redisson.connection.balancer.RoundRobinLoadBalancer` - 轮询调度算法
     * `org.redisson.connection.balancer.RandomLoadBalancer` - 随机调度算法
     */
    private String loadBalancer = "org.redisson.connection.balancer.RoundRobinLoadBalancer";

    /**
     * 从节点最小空闲连接数 默认值 32
     */
    private int slaveConnectionMinimumIdleSize = 32;

    /**
     * 从节点连接池大小 默认值 64
     */
    private int slaveConnectionPoolSize = 64;

    /**
     * 多从节点的环境里，每个 主节点的最小保持连接数（长连接）。长期保持一定数量的连接有利于提高瞬时写入反应速度。默认值 32
     */
    private int masterConnectionMinimumIdleSize = 32;

    /**
     * 主节点的连接池最大容量。连接池的连接数量自动弹性伸缩。 默认值 64
     */
    private int masterConnectionPoolSize = 64;

    /**
     * 读取操作的负载均衡模式 默认值： SLAVE（只在从服务节点里读取 可用值为： SLAVE - 只在从服务节点里读取。 MASTER - 只在主服务节点里读取。 MASTER_SLAVE - 在主从服务节点里都可以读取。
     */
    private String readMode = "SLAVE";

    /**
     * 订阅操作的负载均衡模式 默认值：SLAVE（只在从服务节点里订阅） 可用值为： SLAVE - 只在从服务节点里订阅。 MASTER - 只在主服务节点里订阅。
     */
    private String subscriptionMode = "SLAVE";

    /**
     * 节点地址
     */
    private String nodeAddresses;
}
