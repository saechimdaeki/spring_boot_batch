package com.example.springbatch.api;

import com.example.springbatch.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class RetryConfigurationApi {

    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;

    @Bean
    public Job batchJob1(){
        return jobBuilderFactory.get("batchJob1")
                .incrementer(new RunIdIncrementer())
                .start(step1Api())
                .build();
    }

    @Bean
    public Step step1Api(){
        return stepBuilderFactory.get("step1Api")
                .<String,String>chunk(5)
                .reader(readerApi())
                .processor(processor())
                .writer(items -> items.forEach(System.out::println))
                .faultTolerant()
                .skip(RetryableException.class)
                .skipLimit(2)
                .retry(RetryableException.class)
                .retryLimit(2)
                .retryPolicy(retryPolicyApi())
                .build();
    }

    @Bean
    public ItemProcessor<? super String, String> processor() {
        return new RetryItemProcessor();
    }


    @Bean
    public ListItemReader<String> readerApi(){
        List<String> items= new ArrayList<>();
        for(int i=0; i<30; i++){
            items.add(String.valueOf(i));
        }
        return new ListItemReader<>(items);
    }

    @Bean
    public RetryPolicy retryPolicyApi(){
        Map<Class<? extends Throwable>,Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(RetryableException.class,true);

        return new SimpleRetryPolicy(3,exceptionClass);
    }
}
