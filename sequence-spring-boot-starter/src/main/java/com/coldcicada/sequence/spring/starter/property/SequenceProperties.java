package com.coldcicada.sequence.spring.starter.property;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author coldcicada
 * @Date 2024-06-26 13:33
 * @Description
 */
@Getter
@Setter
@Accessors(chain = true)
@Component
@ConfigurationProperties(prefix = SequenceProperties.PREFIX)
public class SequenceProperties {

    public static final String PREFIX = "sequence";

    //redis客户端Bean名称
    private String redisClient;

    private Map<String, Item> items = new HashMap<>();

}
