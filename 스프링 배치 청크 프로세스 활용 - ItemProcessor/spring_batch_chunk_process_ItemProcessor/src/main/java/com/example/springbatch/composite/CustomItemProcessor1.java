package com.example.springbatch.composite;

import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor1 implements ItemProcessor<String,String> {

    int cnt=0;

    @Override
    public String process(String s) throws Exception {

        cnt++;

        return (s+cnt).toUpperCase();
    }
}
