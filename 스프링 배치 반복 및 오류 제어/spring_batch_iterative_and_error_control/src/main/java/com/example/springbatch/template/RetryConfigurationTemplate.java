package com.example.springbatch.template;

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
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class RetryConfigurationTemplate {

    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;

    @Bean
    public Job batchJob2(){
        return jobBuilderFactory.get("batchJob2")
                .incrementer(new RunIdIncrementer())
                .start(step1Template())
                .build();
    }

    @Bean
    public Step step1Template(){
        return stepBuilderFactory.get("step1Template")
                .<String,Customer>chunk(5)
                .reader(readerTemplate())
                .processor(itemProcessor())
                .writer(items -> items.forEach(System.out::println))
                .faultTolerant()
//                .skip(RetryableException.class)
//                .skipLimit(2)
//                .retry(RetryableException.class)
//                .retryLimit(2)
//                .retryPolicy(retryPolicy())
                .build();
    }




    @Bean
    public ItemProcessor<String,Customer> itemProcessor(){
        return new RetryItemProcessor2();
    }

    @Bean
    public ListItemReader<String> readerTemplate(){
        List<String> items= new ArrayList<>();
        for(int i=0; i<30; i++){
            items.add(String.valueOf(i));
        }
        return new ListItemReader<>(items);
    }

    @Bean
    public RetryPolicy retryPolicyTemplate(){
        Map<Class<? extends Throwable>,Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(RetryableException.class,true);

        return new SimpleRetryPolicy(3,exceptionClass);
    }

    @Bean
    public RetryTemplate retryTemplate(){
        Map<Class<? extends Throwable>,Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(RetryableException.class,true);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000);

        SimpleRetryPolicy simpleRetryPolicy=new SimpleRetryPolicy(2,exceptionClass);
        RetryTemplate retryTemplate=new RetryTemplate();
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
//        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
}
