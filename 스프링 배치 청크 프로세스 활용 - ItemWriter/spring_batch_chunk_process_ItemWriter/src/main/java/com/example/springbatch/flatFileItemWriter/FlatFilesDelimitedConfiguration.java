package com.example.springbatch.flatFileItemWriter;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class FlatFilesDelimitedConfiguration {

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
//                .<Customer,Customer>chunk(10)
//                .reader(customItemReader())
//                .writer(customItemWriter())
//                .build();
//    }
//
//    /**
//     * (delimeted 방식)
//     * @return
//     */
////    @Bean
////    public ItemWriter<? super Customer> customItemWriter() {
////
////        return new FlatFileItemWriterBuilder<>()
////                .name("flatFileWriter")
////                .resource(new FileSystemResource("/Users/kimjunseong/Desktop/spring_batch/스프링 배치 청크 프로세스 활용 - ItemWriter/spring_batch_chunk_process_ItemWriter/src/main/resources/customer.txt"))
////                .append(true)// 이미 있는 데이터에 추가
////                .shouldDeleteIfEmpty(true) // 쓰기 작업할 데이터가 없다면 그 파일을 삭제.
////                .delimited()
////                .delimiter("|")
////                .names(new String[]{"id","name","age"})
////                .build();
////    }
//
//    @Bean
//    public ItemWriter<? super Customer> customItemWriter(){
//
//        return new FlatFileItemWriterBuilder<>()
//                .name("flatFileWriter")
//                .resource(new FileSystemResource("/Users/kimjunseong/Desktop/spring_batch/스프링 배치 청크 프로세스 활용 - ItemWriter/spring_batch_chunk_process_ItemWriter/src/main/resources/customer_format.txt"))
//                .formatted()
//                .format("%-2d%-14s%-2d")
//                .names(new String[]{"id","name","age"})
//                .build();
//    }
//
//    @Bean
//    public ItemReader<? extends Customer> customItemReader() {
//        List<Customer> customers = Arrays.asList(
//                new Customer(1,"honggildong1",21),
//                new Customer(2,"kimjunseong2",24),
//                new Customer(3,"saechimdaeki",29)
//                );
//        ListItemReader<Customer> reader = new ListItemReader<>(customers);
//        return reader;
//    }
}
