package com.example.springbatch.flatfileitemreader.fixedlengthtokenizer;

import com.example.springbatch.flatfileitemreader.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class FlatFilesFixedLengthConfiguration {

//    private final JobBuilderFactory jobBuilderFactory;
//    private final StepBuilderFactory stepBuilderFactory;
//
//    @Bean
//    public Job job() {
//        return jobBuilderFactory.get("batchJob")
//                .start(step1())
//                .next(step2())
//                .incrementer(new RunIdIncrementer())
//                .build();
//    }
//
//    @Bean
//    public Step step1() {
//        return stepBuilderFactory.get("step1")
//                .<String, String>chunk(2)
//                .reader(itemReader())
//                .writer(new ItemWriter() {
//                    @Override
//                    public void write(List items) throws Exception {
//                        System.out.println("items = " + items);
//                    }
//                })
//                .build();
//    }
//
//    @Bean
//    public Step step2() {
//        return stepBuilderFactory.get("step2")
//                .tasklet((contribution, chunkContext) -> {
//                    System.out.println("step2 has executed");
//                    return RepeatStatus.FINISHED;
//                })
//                .build();
//    }
//
//    @Bean
//    public FlatFileItemReader itemReader(){
//        return new FlatFileItemReaderBuilder<Customer>()
//                .name("flatFile")
//                .resource(new FileSystemResource("/Users/kimjunseong/Desktop/spring_batch/스프링 배치 청크 프로세스 활용 - ItemReader/spring_batch_chunk_process_ItemReader/src/main/resources/customer.txt"))
//                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
//                .targetType(Customer.class)
//                .linesToSkip(1)
//                .fixedLength()
//                .addColumns(new Range(1,5))
//                .addColumns(new Range(6,10))
//                .addColumns(new Range(10,11))
//                .names("name","year","age")
//                .build();
//    }
}