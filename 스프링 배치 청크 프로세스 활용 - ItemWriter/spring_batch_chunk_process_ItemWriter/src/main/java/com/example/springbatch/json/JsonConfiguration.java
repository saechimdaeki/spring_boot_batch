package com.example.springbatch.json;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class JsonConfiguration {

//    private final JobBuilderFactory jobBuilderFactory;
//    private final StepBuilderFactory stepBuilderFactory;
//    private final DataSource dataSource;
//
//    @Bean
//    public Job batchJob() throws Exception {
//        return jobBuilderFactory.get("batchJob")
//                .incrementer(new RunIdIncrementer())
//                .start(step1())
//                .build();
//    }
//
//    @Bean
//    public Step step1() throws Exception {
//        return stepBuilderFactory.get("step1")
//                .<Customer_json, Customer_json>chunk(10)
//                .reader(customItemReader())
//                .writer(customItemWriter())
//                .build();
//    }
//
//    @Bean
//    public JdbcPagingItemReader<Customer_json> customItemReader() {
//
//        JdbcPagingItemReader<Customer_json> reader = new JdbcPagingItemReader<>();
//
//        reader.setDataSource(this.dataSource);
//        reader.setFetchSize(10);
//        reader.setRowMapper(new CustomerRowMapper());
//
//        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
//        queryProvider.setSelectClause("id, firstName, lastName, birthdate");
//        queryProvider.setFromClause("from customer");
//        queryProvider.setWhereClause("where firstname like :firstname");
//
//        Map<String, Order> sortKeys = new HashMap<>(1);
//
//        sortKeys.put("id", Order.ASCENDING);
//        queryProvider.setSortKeys(sortKeys);
//        reader.setQueryProvider(queryProvider);
//
//        HashMap<String, Object> parameters = new HashMap<>();
//        parameters.put("firstname", "A%");
//
//        reader.setParameterValues(parameters);
//
//        return reader;
//    }
//
//    @Bean
//    public JsonFileItemWriter<Customer_json> customItemWriter() {
//        return new JsonFileItemWriterBuilder<Customer_json>()
//                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
//                .resource(new FileSystemResource("/Users/kimjunseong/Desktop/spring_batch/스프링 배치 청크 프로세스 활용 - ItemWriter/spring_batch_chunk_process_ItemWriter/src/main/resources/customer_json.json"))
//                .name("customerJsonFileItemWriter")
//                .build();
//    }
}
