package com.coldcicada.redisson.spring.starter.configuration;

import com.coldcicada.redisson.spring.starter.context.RedissonContext;
import com.coldcicada.redisson.spring.starter.property.MultiRedissonProperties;
import com.coldcicada.redisson.spring.starter.property.item.ClusterRedissonProperty;
import com.coldcicada.redisson.spring.starter.property.item.SentinelRedissonProperty;
import com.coldcicada.redisson.spring.starter.property.item.SingleRedissonProperty;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author coldcicada
 * @Date 2024-06-25 17:14
 * @Description
 */
@Configuration
@EnableConfigurationProperties(MultiRedissonProperties.class)
@ConditionalOnExpression("${multi-redisson.enabled:true}")
public class MultiRedissonAutoConfiguration implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MultiRedissonAutoConfiguration.class);

    private static final String DESTROY_METHOD_NAME = "shutdown";

    private static final String SENTINEL_ADDRESS_PREFIX = "redis://";

    private static final String CLIENT_BEAN_SUFFIX = "RedissonClient";

    private static final String CLIENT_MODE_CONFIG_KEY = "multi-redisson.client-model";

    private final ConfigurableListableBeanFactory configurableListableBeanFactory;

    private final ApplicationContext applicationContext;

    private final MultiRedissonProperties properties;

    @Autowired
    public MultiRedissonAutoConfiguration(ConfigurableListableBeanFactory configurableListableBeanFactory, ApplicationContext applicationContext, MultiRedissonProperties properties) {
        this.configurableListableBeanFactory = configurableListableBeanFactory;
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public RedissonConnectionFactory redissonConnectionFactory() {

        RedissonClient redisson = applicationContext.getBean(properties.getPrimary() + CLIENT_BEAN_SUFFIX, RedissonClient.class);

        return new RedissonConnectionFactory(redisson);
    }

    @Bean
    @Lazy
    @ConditionalOnMissingBean(RedissonReactiveClient.class)
    public RedissonReactiveClient redissonReactive() {

        RedissonClient redisson = applicationContext.getBean(properties.getPrimary() + CLIENT_BEAN_SUFFIX, RedissonClient.class);

        return redisson.reactive();
    }

    @Bean
    @Lazy
    @ConditionalOnMissingBean(RedissonRxClient.class)
    public RedissonRxClient redissonRxJava() {

        RedissonClient redisson = applicationContext.getBean(properties.getPrimary() + CLIENT_BEAN_SUFFIX, RedissonClient.class);

        return redisson.rxJava();
    }

    private void registerSingleRedissonClient(Map<String, SingleRedissonProperty> properties) {

        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) configurableListableBeanFactory;

        properties.forEach((k,v) -> {

            Config config = generateSingleConfig(v);

            String redissonBeanClientName = k + CLIENT_BEAN_SUFFIX;

            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(Redisson.class);
            beanDefinitionBuilder.addConstructorArgValue(config);
            beanDefinitionBuilder.setDestroyMethodName(DESTROY_METHOD_NAME);
            beanDefinitionBuilder.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
            beanFactory.registerBeanDefinition(redissonBeanClientName, beanDefinitionBuilder.getRawBeanDefinition());

        });
    }

    private void registerSentinelRedissonClient(Map<String, SentinelRedissonProperty> properties) {

        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) configurableListableBeanFactory;

        properties.forEach((k,v) -> {

            Config config = generateSentinelConfig(v);

            String redissonBeanClientName = k + CLIENT_BEAN_SUFFIX;

            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(Redisson.class);
            beanDefinitionBuilder.addConstructorArgValue(config);
            beanDefinitionBuilder.setDestroyMethodName(DESTROY_METHOD_NAME);
            beanDefinitionBuilder.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
            beanFactory.registerBeanDefinition(redissonBeanClientName, beanDefinitionBuilder.getRawBeanDefinition());

        });
    }


    private void registerClusterRedissonClient(Map<String, ClusterRedissonProperty> properties) {

        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) configurableListableBeanFactory;

        properties.forEach((k,v) -> {

            Config config = generateClusterConfig(v);

            String redissonBeanClientName = k + CLIENT_BEAN_SUFFIX;

            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(Redisson.class);
            beanDefinitionBuilder.addConstructorArgValue(config);
            beanDefinitionBuilder.setDestroyMethodName(DESTROY_METHOD_NAME);
            beanDefinitionBuilder.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
            beanFactory.registerBeanDefinition(redissonBeanClientName, beanDefinitionBuilder.getRawBeanDefinition());

        });
    }


    private Config generateSingleConfig(SingleRedissonProperty property) {

        Config config = new Config();

        String address = property.getAddress().contains(SENTINEL_ADDRESS_PREFIX) ? property.getAddress() : (SENTINEL_ADDRESS_PREFIX+property.getAddress());

        config.useSingleServer()
                .setAddress(address)
                .setDatabase(property.getDatabase())
                .setIdleConnectionTimeout(property.getIdleConnectionTimeout())
                .setConnectTimeout(property.getConnectTimeout())
                .setTimeout(property.getTimeout())
                .setRetryAttempts(property.getRetryAttempts())
                .setRetryInterval(property.getRetryInterval())
                .setClientName(ObjectUtils.isEmpty(property.getClientName()) ? null : property.getClientName())
                .setPassword(ObjectUtils.isEmpty(property.getPassword()) ? null : property.getPassword())
                .setSubscriptionConnectionMinimumIdleSize(property.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(property.getSubscriptionConnectionPoolSize());

        try {
            return getConfig(config, property.getCodec(), property.getThreads(), property.getNettyThreads());
        } catch (Exception exception) {
            logger.error("初始化SingleRedissonClient异常",exception);
            return null;
        }
    }

    private Config generateSentinelConfig(SentinelRedissonProperty property) {

        List<String> sentinelAddress = Arrays.stream(property.getSentinelAddresses().split(",")).map(item -> item.contains(SENTINEL_ADDRESS_PREFIX) ? item : (SENTINEL_ADDRESS_PREFIX+item)).toList();

        Config config = new Config();

        config.useSentinelServers()
                .setCheckSentinelsList(false)
                .setMasterName(property.getMasterName())
                .addSentinelAddress(sentinelAddress.toArray(new String[]{}))
                .setDatabase(property.getDatabase())
                .setIdleConnectionTimeout(property.getIdleConnectionTimeout())
                .setConnectTimeout(property.getConnectTimeout())
                .setTimeout(property.getTimeout())
                .setRetryAttempts(property.getRetryAttempts())
                .setRetryInterval(property.getRetryInterval())
                .setFailedSlaveReconnectionInterval(property.getFailedSlaveReconnectionInterval())
                .setClientName(ObjectUtils.isEmpty(property.getClientName()) ? null : property.getClientName())
                .setPassword(ObjectUtils.isEmpty(property.getPassword()) ? null : property.getPassword())
                .setSubscriptionConnectionMinimumIdleSize(property.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(property.getSubscriptionConnectionPoolSize())
                .setSlaveConnectionMinimumIdleSize(property.getSlaveConnectionMinimumIdleSize())
                .setSlaveConnectionPoolSize(property.getSlaveConnectionPoolSize())
                .setMasterConnectionMinimumIdleSize(property.getMasterConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(property.getMasterConnectionPoolSize())
                .setReadMode(ReadMode.valueOf(property.getReadMode()))
                .setSubscriptionMode(SubscriptionMode.valueOf(property.getSubscriptionMode()));

        try {

            return getConfig(config, property.getCodec(), property.getThreads(), property.getNettyThreads());

        } catch (Exception exception) {
            logger.error("初始化SentinelRedissonClient异常",exception);
            return null;
        }
    }

    private Config generateClusterConfig(ClusterRedissonProperty property) {

        Config config = new Config();

        List<String> nodeAddress = Arrays.stream(property.getNodeAddresses().split(",")).map(item -> item.contains(SENTINEL_ADDRESS_PREFIX) ? item : (SENTINEL_ADDRESS_PREFIX+item)).toList();

        config.useClusterServers()
                .addNodeAddress(nodeAddress.toArray(new String[]{}))
                .setIdleConnectionTimeout(property.getIdleConnectionTimeout())
                .setConnectTimeout(property.getConnectTimeout())
                .setTimeout(property.getTimeout())
                .setRetryAttempts(property.getRetryAttempts())
                .setRetryInterval(property.getRetryInterval())
                .setFailedSlaveReconnectionInterval(property.getFailedSlaveReconnectionInterval())
                .setClientName(ObjectUtils.isEmpty(property.getClientName()) ? null : property.getClientName())
                .setPassword(ObjectUtils.isEmpty(property.getPassword()) ? null : property.getPassword())
                .setSubscriptionConnectionMinimumIdleSize(property.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(property.getSubscriptionConnectionPoolSize())
                .setSlaveConnectionMinimumIdleSize(property.getSlaveConnectionMinimumIdleSize())
                .setSlaveConnectionPoolSize(property.getSlaveConnectionPoolSize())
                .setMasterConnectionMinimumIdleSize(property.getMasterConnectionMinimumIdleSize())
                .setMasterConnectionPoolSize(property.getMasterConnectionPoolSize())
                .setReadMode(ReadMode.valueOf(property.getReadMode()))
                .setSubscriptionMode(SubscriptionMode.valueOf(property.getSubscriptionMode()));

        try {
            return getConfig(config, property.getCodec(), property.getThreads(), property.getNettyThreads());
        } catch (Exception exception) {
            logger.error("初始化ClusterRedissonClient异常",exception);
            return null;
        }
    }

    private Config getConfig(Config config, String codec, int threads, int nettyThreads) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        Codec code = (Codec) ClassUtils.forName(codec, ClassUtils.getDefaultClassLoader()).getDeclaredConstructor().newInstance();

        config.setCodec(code);

        if (!ObjectUtils.isEmpty(threads)) {
            config.setThreads(threads);
        }

        if(!ObjectUtils.isEmpty(nettyThreads)) {
            config.setNettyThreads(nettyThreads);
        }
        return config;
    }

    @Override
    public void afterPropertiesSet(){

        if (!ObjectUtils.isEmpty(properties)) {

            String clientMode = applicationContext.getEnvironment().getProperty(CLIENT_MODE_CONFIG_KEY);

            if (clientMode != null) {

                switch (clientMode) {
                    case "single":

                        registerSingleRedissonClient(properties.getSingleClients());
                        properties.getSingleClients().forEach((k, v) -> RedissonContext.getRedissonClientMap().put(k + CLIENT_BEAN_SUFFIX, applicationContext.getBean(k + CLIENT_BEAN_SUFFIX, RedissonClient.class)));

                        break;
                    case "sentinel":

                        registerSentinelRedissonClient(properties.getSentinelClients());
                        properties.getSentinelClients().forEach((k, v) -> RedissonContext.getRedissonClientMap().put(k + CLIENT_BEAN_SUFFIX, applicationContext.getBean(k + CLIENT_BEAN_SUFFIX, RedissonClient.class)));

                        break;
                    case "cluster":

                        registerClusterRedissonClient(properties.getClusterClients());
                        properties.getClusterClients().forEach((k, v) -> RedissonContext.getRedissonClientMap().put(k + CLIENT_BEAN_SUFFIX, applicationContext.getBean(k + CLIENT_BEAN_SUFFIX, RedissonClient.class)));
                        break;

                    default:

                }
            }

            RedissonContext.setApplicationContext(applicationContext);
        }
    }
}
