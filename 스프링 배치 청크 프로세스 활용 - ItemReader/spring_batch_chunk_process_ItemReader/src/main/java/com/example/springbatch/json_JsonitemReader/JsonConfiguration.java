package com.example.springbatch.json_JsonitemReader;

import com.example.springbatch.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;



@RequiredArgsConstructor
@Configuration
public class JsonConfiguration {

//    private final JobBuilderFactory jobBuilderFactory;
//    private final StepBuilderFactory stepBuilderFactory;
//
//    @Bean
//    public Job batchJob(){
//        return jobBuilderFactory.get("batchJob")
//                .incrementer(new RunIdIncrementer())
//                .start(step1())
//                .build();
//    }
//
//    @Bean
//    public Step step1(){
//        return stepBuilderFactory.get("step1")
//                .<Customer,Customer>chunk(3)
//                .reader(customItemReader())
//                .writer(customerItemWriter())
//                .build();
//    }
//
//    @Bean
//    public ItemReader<? extends Customer> customItemReader() {
//        return new JsonItemReaderBuilder<Customer>()
//                .name("jsonReader")
//                .resource(new ClassPathResource("customer.json"))
//                .jsonObjectReader(new JacksonJsonObjectReader<>(Customer.class))
//                .build();
//    }
//
//
//    @Bean
//    public ItemWriter<Customer> customerItemWriter(){
//        return items->{
//            for (Customer item : items) {
//                System.out.println(item.toString());
//            }
//        };
//    }
}
