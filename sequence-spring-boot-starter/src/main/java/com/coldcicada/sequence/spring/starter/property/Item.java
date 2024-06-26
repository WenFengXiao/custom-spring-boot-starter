package com.coldcicada.sequence.spring.starter.property;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author coldcicada
 * @Date 2024-06-26 13:38
 * @Description
 */
@Getter
@Setter
@Accessors(chain = true)
public class Item {

    //流水分类
    private String kind;

    //前缀
    private String prefix = "";

    //当前日期
    private String cDate;

    //当前值
    private int cValue;

    //步长
    private int sValue;

    //长度 不包括前缀的长度，不足需前面补充到指定的长度
    private int	len;

    //填充字符
    private String fChar;

    //L: 左边填充 R: 右边填充
    private String fOrient;

    //预取数量
    private int prefetch;


    public static final class ItemBuilder {
        private Item item;

        private ItemBuilder() {
            item = new Item();
        }

        public static ItemBuilder create() {
            return new ItemBuilder();
        }

        public ItemBuilder withKind(String kind) {
            item.setKind(kind);
            return this;
        }

        public ItemBuilder withPrefix(String prefix) {
            item.setPrefix(prefix);
            return this;
        }

        public ItemBuilder withCDate(String cDate) {
            item.setCDate(cDate);
            return this;
        }

        public ItemBuilder withCValue(int cValue) {
            item.setCValue(cValue);
            return this;
        }

        public ItemBuilder withSValue(int sValue) {
            item.setSValue(sValue);
            return this;
        }

        public ItemBuilder withLen(int len) {
            item.setLen(len);
            return this;
        }

        public ItemBuilder withFChar(String fChar) {
            item.setFChar(fChar);
            return this;
        }

        public ItemBuilder withFOrient(String fOrient) {
            item.setFOrient(fOrient);
            return this;
        }

        public ItemBuilder withPrefetch(int prefetch) {
            item.setPrefetch(prefetch);
            return this;
        }

        public Item build() {
            return item;
        }
    }
}
