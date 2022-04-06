package com.example.spring_batch.job_step;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;import org.springframework.stereotype.Component;

@Component
public class CustomJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution JobExecution) {
        System.out.println("JobExecution.getJobName() : " + JobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution JobExecution) {
        System.out.println("JobExecution.getStatus() : " + JobExecution.getStatus());
    }
}