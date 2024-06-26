package com.coldcicada.sequence.spring.starter.utils;

/**
 * @Author coldcicada
 * @Date 2024-06-26 16:21
 * @Description
 */
public class SequenceFactory {

    private SequenceFactory() {
    }

    private static class SequenceManagerHolder{
        private static final SequenceGenerator INSTANCE = new SequenceGenerator();
    }

    public static SequenceGenerator getInstance(){

        return SequenceManagerHolder.INSTANCE;
    }

}
