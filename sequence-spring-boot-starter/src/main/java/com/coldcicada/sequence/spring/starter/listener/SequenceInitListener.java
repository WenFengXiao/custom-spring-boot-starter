package com.coldcicada.sequence.spring.starter.listener;

import com.coldcicada.redisson.spring.starter.context.RedissonCommonConstant;
import com.coldcicada.redisson.spring.starter.context.RedissonContext;
import com.coldcicada.sequence.spring.starter.context.SequenceCommonConstant;
import com.coldcicada.sequence.spring.starter.context.SequenceContext;
import com.coldcicada.sequence.spring.starter.property.Item;
import com.coldcicada.sequence.spring.starter.property.SequenceProperties;
import com.coldcicada.sequence.spring.starter.utils.SequenceFactory;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author coldcicada
 * @Date 2024-06-26 13:54
 * @Description
 */
@Component
public class SequenceInitListener implements ApplicationListener<ApplicationStartedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SequenceInitListener.class);


    private SequenceProperties sequenceProperties;

    @Autowired
    public SequenceInitListener(SequenceProperties sequenceProperties) {
        this.sequenceProperties = sequenceProperties;
    }


    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        sequenceScriptLoad();
        serializationInit();
    }


    private void sequenceScriptLoad() {

        RedissonClient redissonClient = SequenceContext.getRedissonClient();

        if (!ObjectUtils.isEmpty(redissonClient)) {

            DefaultRedisScript<String> initSequenceScript = new DefaultRedisScript<>();
            initSequenceScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("/InitSequence.lua")));

            DefaultRedisScript<String> getNextSequenceScript = new DefaultRedisScript<>();
            getNextSequenceScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("/getNextSequence.lua")));

            String initSequenceScriptSha = redissonClient.getScript(new StringCodec()).scriptLoad(initSequenceScript.getScriptAsString());

            RedissonContext.getRedisScriptShaMap().put(SequenceCommonConstant.INIT_SEQUENCE_KEY,initSequenceScriptSha);

            logger.info("加载初始化序列号Redis脚本：{}-{}", SequenceCommonConstant.INIT_SEQUENCE_KEY, initSequenceScriptSha);


            String getNextSequenceScriptSha = redissonClient.getScript(new StringCodec()).scriptLoad(getNextSequenceScript.getScriptAsString());

            RedissonContext.getRedisScriptShaMap().put(SequenceCommonConstant.GET_NEXT_SEQUENCE_KEY,getNextSequenceScriptSha);

            logger.info("加载获取下一序列号Redis脚本：{}-{}", SequenceCommonConstant.GET_NEXT_SEQUENCE_KEY, getNextSequenceScriptSha);

        }
    }

    private void serializationInit() {

        RedissonClient redissonClient = SequenceContext.getRedissonClient();

        String systemNo = SequenceContext.getApplicationContext().getEnvironment().getProperty(SequenceCommonConstant.SEQUENCE_SYSTEM_NUMBER_CONFIG_KEY, SequenceContext.getApplicationContext().getEnvironment().getProperty(RedissonCommonConstant.SYSTEM_NUMBER_CONFIG_KEY, ""));

        if(StringUtils.isEmpty(systemNo)) {
            throw new RuntimeException("未读取到配置: " + RedissonCommonConstant.SYSTEM_NUMBER_CONFIG_KEY + " 或者 " + SequenceCommonConstant.SEQUENCE_SYSTEM_NUMBER_CONFIG_KEY);
        }

        if(!ObjectUtils.isEmpty(redissonClient) && !ObjectUtils.isEmpty(sequenceProperties) && !ObjectUtils.isEmpty(sequenceProperties.getItems())) {

            for (Map.Entry<String, Item> entry : sequenceProperties.getItems().entrySet()) {

                //LocalDate localDate = DateUtils.getCurrentTimeFormRedis(redissonClient).toLocalDate();

                LocalDate localDate = LocalDate.now();

                String kind = entry.getKey();
                Item item = entry.getValue();

                String serializationRedisKey = SequenceFactory.getInstance().sequenceRedisKeyGenerator(kind);

                List<Object> initResult = redissonClient.getScript(new StringCodec()).evalSha(RScript.Mode.READ_WRITE, RedissonContext.getRedisScriptShaMap().get(SequenceCommonConstant.INIT_SEQUENCE_KEY),RScript.ReturnType.MULTI,
                        Collections.singletonList(serializationRedisKey),
                        item.getPrefix(),
                        localDate.format(DateTimeFormatter.BASIC_ISO_DATE),
                        0,
                        item.getSValue(),
                        item.getLen(),
                        item.getFChar(),
                        item.getFOrient(),
                        item.getPrefetch());

                List<Object> result = redissonClient.getScript(new StringCodec()).evalSha(RScript.Mode.READ_WRITE, RedissonContext.getRedisScriptShaMap().get(SequenceCommonConstant.GET_NEXT_SEQUENCE_KEY), RScript.ReturnType.MULTI, Collections.singletonList(serializationRedisKey), localDate.format(DateTimeFormatter.BASIC_ISO_DATE));

                Item resultItem = generate(kind, result);

                SequenceContext.getItemMap().put(kind, resultItem);

                ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();

                queue.addAll(Stream.iterate(0 + resultItem.getSValue(), i -> i + resultItem.getSValue()).limit(resultItem.getPrefetch()).map(i-> resultItem.getCDate()+ SequenceCommonConstant.HYPHEN  + (resultItem.getCValue()+i)).collect(Collectors.toList()));

                SequenceContext.getSequenceIndexMap().put(kind, queue);

                logger.info("初始化Serialization：{}", kind);
            }
        }
    }

    public Item generate(String kind, List<Object> luaResult) {

        return Item.ItemBuilder.create()
                .withKind(kind)
                .withPrefix(luaResult.get(0).toString())
                .withCDate(luaResult.get(1).toString())
                .withCValue(Integer.parseInt(luaResult.get(2).toString()))
                .withSValue(Integer.parseInt(luaResult.get(3).toString()))
                .withLen(Integer.parseInt(luaResult.get(4).toString()))
                .withFChar(luaResult.get(5).toString())
                .withFOrient(luaResult.get(6).toString())
                .withPrefetch(Integer.parseInt(luaResult.get(7).toString())).build();
    }

}
