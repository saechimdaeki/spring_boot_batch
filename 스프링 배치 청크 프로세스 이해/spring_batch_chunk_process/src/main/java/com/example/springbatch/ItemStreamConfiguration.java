package com.example.springbatch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class ItemStreamConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job(){
        return jobBuilderFactory.get("batchJob")
                .start(step1())
                .next(step2())
                .incrementer(new RunIdIncrementer())
                .build();
    }


    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .<String,String>chunk(3)
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public CustomItemStreamReader itemReader(){
        List<String> items=new ArrayList<>(10);

        for(int i=0; i<10; i++){
            items.add(String.valueOf(i));
        }
        return new CustomItemStreamReader(items);
    }
    @Bean
    public ItemWriter<? super String> itemWriter(){
        return new CustomItemWriter();
    }

    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("step2 was executed");
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }
}
