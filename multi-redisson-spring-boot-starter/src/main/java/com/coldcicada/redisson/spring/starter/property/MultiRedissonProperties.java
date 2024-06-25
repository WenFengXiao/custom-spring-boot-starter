package com.coldcicada.redisson.spring.starter.property;

import com.coldcicada.redisson.spring.starter.property.item.ClusterRedissonProperty;
import com.coldcicada.redisson.spring.starter.property.item.SentinelRedissonProperty;
import com.coldcicada.redisson.spring.starter.property.item.SingleRedissonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author coldcicada
 * @Date 2024-06-25 16:56
 * @Description
 */
@Getter
@Setter
@ConfigurationProperties(prefix = MultiRedissonProperties.PREFIX)
public class MultiRedissonProperties {

    public static final String PREFIX = "multi-redisson";

    private String primary;

    private Map<String, SentinelRedissonProperty> sentinelClients = new LinkedHashMap<>();

    private Map<String, SingleRedissonProperty> singleClients = new LinkedHashMap<>();

    private Map<String, ClusterRedissonProperty> clusterClients = new LinkedHashMap<>();

}
