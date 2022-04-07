package com.example.spring_batch;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

@SpringBatchTest
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes={SimpleJobConfiguration.class, TestBatchConfig.class})
public class SimpleJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void simple_job_테스트() throws Exception {

        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("requestDate", "20020101")
                .addLong("date", new Date().getTime())
                .toJobParameters();

        // when
//        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("step1");

        // then
        Assertions.assertEquals(jobExecution.getStatus(), BatchStatus.COMPLETED);
        StepExecution stepExecution = (StepExecution)((List) jobExecution.getStepExecutions()).get(0);

        Assertions.assertEquals(stepExecution.getCommitCount(), 11);
        Assertions.assertEquals(stepExecution.getWriteCount(), 1000);
        Assertions.assertEquals(stepExecution.getWriteCount(), 1000);
    }

    @AfterEach
    void clear() throws Exception {
        jdbcTemplate.execute("delete from customer2");
    }
}