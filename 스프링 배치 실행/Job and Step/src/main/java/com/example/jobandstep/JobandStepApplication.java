package com.example.jobandstep;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class JobandStepApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobandStepApplication.class, args);
    }

}
