package com.coldcicada.sequence.spring.starter.configuration;

import com.coldcicada.redisson.spring.starter.configuration.MultiRedissonAutoConfiguration;
import com.coldcicada.sequence.spring.starter.context.SequenceContext;
import com.coldcicada.sequence.spring.starter.property.SequenceProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

/**
 * @Author coldcicada
 * @Date 2024-06-26 13:59
 * @Description
 */
@Configuration
@EnableConfigurationProperties(SequenceProperties.class)
@AutoConfigureAfter(MultiRedissonAutoConfiguration.class)
@ConditionalOnExpression("${customise-sequence.enabled:true}")
public class SequenceAutoConfiguration implements InitializingBean {

    @Autowired
    private SequenceProperties sequenceProperties;

    @Autowired
    private ApplicationContext applicationContext;


    @Override
    public void afterPropertiesSet(){

        SequenceContext.setApplicationContext(applicationContext);

        if (!ObjectUtils.isEmpty(sequenceProperties)) {
            SequenceContext.setSequenceProperty(sequenceProperties);
        }

    }

}
