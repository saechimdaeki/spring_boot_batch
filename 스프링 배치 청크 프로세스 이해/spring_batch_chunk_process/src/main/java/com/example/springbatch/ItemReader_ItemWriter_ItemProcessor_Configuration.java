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
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class ItemReader_ItemWriter_ItemProcessor_Configuration {

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
    @Data
    @AllArgsConstructor
    static class Customer{
        private String name;
    }

    static class CustomItemReader implements ItemReader<Customer>{

        private List<Customer> list;

        public CustomItemReader(List<Customer> list) {
            this.list = new ArrayList<>(list);
        }

        @Override
        public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
            if(!list.isEmpty()){
                return list.remove(0);
            }
            return null;
        }
    }

    static class CustomItemProcessor implements ItemProcessor<Customer,Customer>{

        @Override
        public Customer process(Customer customer) throws Exception {
            customer.setName(customer.getName().toUpperCase());
            return customer;
        }
    }

    static class CustomItemWriter implements ItemWriter<Customer>{

        @Override
        public void write(List<? extends Customer> list) throws Exception {
            list.forEach(item -> System.out.println(item));
        }
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .<Customer,Customer>chunk(3)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public ItemReader<Customer> itemReader(){
        return new CustomItemReader(Arrays.asList(new Customer("user1"),
                new Customer("user2"),
                new Customer("user3")));
    }

    @Bean
    public ItemProcessor<? super Customer, ? extends Customer> itemProcessor(){
        return new CustomItemProcessor();
    }

    @Bean
    public ItemWriter<? super Customer> itemWriter(){
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
