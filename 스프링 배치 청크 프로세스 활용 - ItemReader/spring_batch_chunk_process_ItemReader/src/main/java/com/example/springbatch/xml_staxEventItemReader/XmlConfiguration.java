package com.example.springbatch.xml_staxEventItemReader;

import com.example.springbatch.flatfileitemreader.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class XmlConfiguration {

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
//    public StaxEventItemReader<Customer> customItemReader() {
//        return new StaxEventItemReaderBuilder<Customer>()
//                .name("xmlFileItemReader")
//                .resource(new ClassPathResource("customer.xml"))
//                .addFragmentRootElements("customer")
//                .unmarshaller(itemMarshaller())
//                .build();
//    }
//
//    @Bean
//    public XStreamMarshaller itemMarshaller() {
//        Map<String, Class<?>> aliases = new HashMap<>();
//        aliases.put("customer", Customer.class);
//        aliases.put("id", Long.class);
//        aliases.put("name", String.class);
//        aliases.put("age", Integer.class);
//        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
//        xStreamMarshaller.setAliases(aliases);
//        return xStreamMarshaller;
//    }
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
