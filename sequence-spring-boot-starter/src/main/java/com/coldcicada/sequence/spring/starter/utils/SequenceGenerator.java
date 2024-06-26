package com.coldcicada.sequence.spring.starter.utils;

import com.coldcicada.redisson.spring.starter.context.RedissonCommonConstant;
import com.coldcicada.redisson.spring.starter.context.RedissonContext;
import com.coldcicada.sequence.spring.starter.context.SequenceCommonConstant;
import com.coldcicada.sequence.spring.starter.context.SequenceContext;
import com.coldcicada.sequence.spring.starter.property.Item;
import com.coldcicada.sequence.spring.starter.utils.subsidiary.SequenceGenerateAuxiliaryService;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author coldcicada
 * @Date 2024-06-26 14:08
 * @Description
 */
public class SequenceGenerator {

    private final String systemNo;

    private final String sequenceRedisKeyPrefix;

    private final RedissonClient redissonClient;

    public SequenceGenerator() {

        systemNo = SequenceContext.getApplicationContext().getEnvironment().getProperty(SequenceCommonConstant.SEQUENCE_SYSTEM_NUMBER_CONFIG_KEY, SequenceContext.getApplicationContext().getEnvironment().getProperty(RedissonCommonConstant.SYSTEM_NUMBER_CONFIG_KEY, ""));

        sequenceRedisKeyPrefix = ObjectUtils.isEmpty(SequenceContext.getApplicationContext().getEnvironment().getProperty(SequenceCommonConstant.SEQUENCE_REDIS_KEY_PREFIX)) ? "" : SequenceContext.getApplicationContext().getEnvironment().getProperty(SequenceCommonConstant.SEQUENCE_REDIS_KEY_PREFIX);

        if(StringUtils.isEmpty(systemNo)) {

            throw new RuntimeException("未读取到配置: " + SequenceCommonConstant.SEQUENCE_SYSTEM_NUMBER_CONFIG_KEY + " 或者 " + RedissonCommonConstant.SYSTEM_NUMBER_CONFIG_KEY);
        }

        redissonClient = SequenceContext.getRedissonClient();
    }

    /**
     * @Description: 获取当前流水号
     * @param kind
     * @Return java.lang.String
     * @Author: coldcicada
     * @Date: 2024/6/26
     */
    public String currentSequence(String kind) {

        sequenceConfigCheck(kind);

        Item item = SequenceContext.itemMap.get(kind);

        if(item.getPrefetch() == 1) {
            return getPrefetchOneSequenceId(kind, true);
        } else {
            return getSequenceId(kind, true);
        }
    }


    /**
     * @Description: 获取下一流水号
     * @param kind
     * @Return java.lang.String
     * @Author: coldcicada
     * @Date: 2024/6/26
     */
    public String nextSequence(String kind) {

        sequenceConfigCheck(kind);

        Item item = SequenceContext.itemMap.get(kind);

        if(item.getPrefetch() == 1) {
            return getPrefetchOneSequenceId(kind, false);
        } else {
            return getSequenceId(kind, false);
        }
    }

    /**
     * @Description: 获取预取为一的索引
     * @param kind
     * @param isGetCurrent
     * @Return java.lang.String
     * @Author: coldcicada
     * @Date: 2024/6/26
     */
    private String getPrefetchOneSequenceId(String kind, boolean isGetCurrent) {


        String serializationNo = "";

        if (isGetCurrent) {
            serializationNo = SequenceContext.sequenceIndexMap.get(kind).peek();
        } else {

            Item item = getItemFromRedis(kind,LocalDate.now());

            serializationNo = SequenceGenerateAuxiliaryService.generateSerialization(item,item.getCDate(),item.getCValue());

            SequenceContext.itemMap.replace(item.getKind(), item);
        }

        return serializationNo;
    }

    /**
     * @Description: 获取索引
     * @param kind
     * @param isGetCurrent
     * @Return java.lang.String
     * @Author: coldcicada
     * @Date: 2024/6/26
     */
    private String getSequenceId(String kind, boolean isGetCurrent) {

        LocalDate localDate = LocalDate.now();

        Item item = SequenceContext.getItemMap().get(kind);

        String sequenceId = "";

        if (isGetCurrent) {

            String[] queueStr = SequenceContext.sequenceIndexMap.get(kind).peek().split(SequenceCommonConstant.HYPHEN);

            return SequenceGenerateAuxiliaryService.generateSerialization(item, queueStr[0], Integer.parseInt(queueStr[1]));

        } else {

            if(SequenceContext.sequenceIndexMap.get(item.getKind()).isEmpty()) {

                Item redisItem  = getItemFromRedis(item.getKind(),localDate);

                //SerializationContext.serializationIndexMap.get(kind).addAll(Stream.iterate(0 + redisSerialize.getSvalue(), i -> i + redisSerialize.getSvalue()).limit(redisSerialize.getPrefetch()).map(i-> SerializationGenerateAuxiliaryService.generateSerialization(redisSerialize, redisSerialize.getCvalue() + i)).collect(Collectors.toList()));

                SequenceContext.sequenceIndexMap.get(kind).addAll(Stream.iterate(redisItem.getSValue(), i -> i + redisItem.getSValue()).limit(redisItem.getPrefetch()).map(i-> redisItem.getCDate()+ SequenceCommonConstant.HYPHEN  + (redisItem.getCValue()+i)).collect(Collectors.toList()));

                SequenceContext.itemMap.replace(item.getKind(), redisItem);

                item = redisItem;
            }

            String queueString = SequenceContext.sequenceIndexMap.get(item.getKind()).poll();

            while (queueString == null) {
                queueString = SequenceContext.sequenceIndexMap.get(item.getKind()).poll();
            }

            String[] queueStr = queueString.split(SequenceCommonConstant.HYPHEN);

            sequenceId = SequenceGenerateAuxiliaryService.generateSerialization(item,queueStr[0], Integer.parseInt(queueStr[1]));
        }

        if(sequenceId.isEmpty()) {
            throw new RuntimeException("获取流水号异常");
        }

        return sequenceId;
    }


    /**
     * @Description: 从redis中获取流水号信息
     * @param kind
     * @param localDate
     * @Return com.coldcicada.sequence.spring.starter.property.Item
     * @Author: coldcicada
     * @Date: 2024/6/26
     */
    private Item getItemFromRedis(String kind, LocalDate localDate) {

        try {

            String sequenceRedisKey  = sequenceRedisKeyGenerator(kind);

            List<Object> result = redissonClient.getScript(new StringCodec()).evalSha(RScript.Mode.READ_WRITE, RedissonContext.getRedisScriptShaMap().get(SequenceCommonConstant.GET_NEXT_SEQUENCE_KEY), RScript.ReturnType.MULTI, Collections.singletonList(sequenceRedisKey), localDate.format(DateTimeFormatter.BASIC_ISO_DATE));

            Item item = generate(kind, result);

            return item;
        } catch (Exception exception) {
            throw new RuntimeException("获取流水号reids脚本执行失败");
        }
    }


    /**
     * @Description: 生成流水号在Redis中的KEY
     * @param kind
     * @Return java.lang.String
     * @Author: coldcicada
     * @Date: 2024/6/26
     */
    public String sequenceRedisKeyGenerator(String kind) {
        StringBuilder sequenceRedisKey = new StringBuilder();

        sequenceRedisKey.append(systemNo).append(RedissonCommonConstant.REDIS_KEY_SEPARATOR);

        if (!sequenceRedisKeyPrefix.isEmpty()) {
           sequenceRedisKey.append(sequenceRedisKeyPrefix).append(RedissonCommonConstant.REDIS_KEY_SEPARATOR);
        }

       sequenceRedisKey.append(kind);

        return sequenceRedisKey.toString();
    }

    /**
     * @Description: lua脚本转换为序号配置对象
     * @param kind
     * @param luaResult
     * @Return com.coldcicada.sequence.spring.starter.property.Item
     * @Author: coldcicada
     * @Date: 2024/6/26
     */
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


    /**
     * 流水号配置检查
     * @param kind 流水号类型码
     */
    private void sequenceConfigCheck(String kind) {

        if(! SequenceContext.getSequenceProperties().getItems().containsKey(kind)) {
            throw new RuntimeException("需要获取序列索引在配置中不存在");
        }
    }
}
