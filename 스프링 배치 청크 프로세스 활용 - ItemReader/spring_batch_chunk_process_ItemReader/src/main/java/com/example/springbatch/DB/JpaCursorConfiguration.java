package com.example.springbatch.DB;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class JpaCursorConfiguration {
//
//    private final JobBuilderFactory jobBuilderFactory;
//    private final StepBuilderFactory stepBuilderFactory;
//
//    private final EntityManagerFactory entityManagerFactory;
//
//    @Bean
//    public Job batchJob(){
//        return jobBuilderFactory.get("batchJob")
//                .start(step1())
//                .incrementer(new RunIdIncrementer())
//                .build();
//    }
//
//    @Bean
//    public Step step1(){
//        return stepBuilderFactory.get("step1")
//                .<CustomerEntity,CustomerEntity>chunk(5)
//                .reader(customItemReader())
//                .writer(customItemWriter())
//                .build();
//    }
//
//    @Bean
//    public ItemReader<? extends CustomerEntity> customItemReader() {
//
//        Map<String,Object> parameters = new HashMap<>();
//        parameters.put("firstname","A%");
//
//        return new JpaCursorItemReaderBuilder<CustomerEntity>()
//                .name("jpaCursorItemReader")
//                .entityManagerFactory(entityManagerFactory)
//                .queryString("select c from CustomerEntity c where firstname like :firstname")
//                .parameterValues(parameters)
//                .build();
//    }
//
//
//    @Bean
//    public ItemWriter<CustomerEntity> customItemWriter(){
//        return items->{
//            for(CustomerEntity item: items){
//                System.out.println(item.toString());
//            }
//        };
//    }
}
