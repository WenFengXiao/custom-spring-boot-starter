package com.coldcicada.sequence.spring.starter.utils.subsidiary;

import com.coldcicada.sequence.spring.starter.property.Item;
import org.springframework.util.ObjectUtils;

/**
 * @Author coldcicada
 * @Date 2024-06-26 14:38
 * @Description
 */
public class SequenceGenerateAuxiliaryService {

    public static String generateSerialization(Item item, String cDate, Integer indexValue) {

        StringBuilder buffer = new StringBuilder();

        buffer.append(ObjectUtils.isEmpty(item.getPrefix()) ? "" : item.getPrefix()).append(cDate).append(item.getKind());

        String targetStr = indexValue + "";

        String fillSide;
        if (item.getFOrient().charAt(0) == 'L') {
            fillSide = "left";
        } else {
            fillSide = "right";
        }

        targetStr = charFill(targetStr, item.getFChar().charAt(0),fillSide,item.getLen());

        buffer.append(targetStr);

        return buffer.toString();

    }


    /**
     * 左、右、两边填充字符
     *
     * @param str      待填充的字符串，可以为null
     * @param fillChar 填充的字符
     * @param fillSide ['left','right','both']
     *                 填充的方向，
     * @param size     输出字符串的固定byte长度。
     * @return String
     */
    public static String charFill(String str, char fillChar, String fillSide, int size) {
        str = (str == null) ? "" : str;
        StringBuilder sb = new StringBuilder(str);
        int len = str.length();
        if (len >= size) {
            return (("left".equals(fillSide)) ? str.substring(len - size) : str.substring(0, size));
        }

        int n = size - len;
        if ("left".equals(fillSide)) {
            for (int i = 0; i < n; ++i) {
                sb.insert(0, fillChar);
            }

        } else if ("right".equals(fillSide)) {
            for (int i = 0; i < n; ++i) {
                sb.append(fillChar);
            }

        }
        return sb.toString();
    }
}
