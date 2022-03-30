package com.example.springbatch.xml;

import com.example.springbatch.flatFileItemWriter.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class XMLConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job batchJob(){
        return jobBuilderFactory.get("batchJob")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .build();
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .<Customer_xml,Customer_xml>chunk(10)
                .reader(customItemReader())
                .writer(customWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer_xml> customWriter() {
        return new StaxEventItemWriterBuilder<Customer_xml>()
                .name("staxEventWriter")
                .marshaller(itemMarshaller())
                .resource(new FileSystemResource("/Users/kimjunseong/Desktop/spring_batch/스프링 배치 청크 프로세스 활용 - ItemWriter/spring_batch_chunk_process_ItemWriter/src/main/resources/customer_xml.xml"))
                .rootTagName("customer_xml")
                .build();
    }

    @Bean
    public Marshaller itemMarshaller() {

        Map<String,Class<?>> aliases = new HashMap<>();
        aliases.put("customer_xml",Customer_xml.class);
        aliases.put("id",Long.class);
        aliases.put("firstName",String.class);
        aliases.put("lastName",String.class);
        aliases.put("birthdate",String.class);

        XStreamMarshaller xStreamMarshaller=new XStreamMarshaller();
        xStreamMarshaller.setAliases(aliases);
        return xStreamMarshaller;
    }

    @Bean
    public JdbcPagingItemReader<Customer_xml> customItemReader(){
        JdbcPagingItemReader<Customer_xml> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(dataSource);
        reader.setFetchSize(10);
        reader.setRowMapper(new CustomerRowMapper());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, firstName, lastName, birthdate");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("where firstname like :firstname");

        Map<String, Order> sortKeys = new HashMap<>(1);

        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);
        reader.setQueryProvider(queryProvider);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "A%");

        reader.setParameterValues(parameters);

        return reader;
    }
}
