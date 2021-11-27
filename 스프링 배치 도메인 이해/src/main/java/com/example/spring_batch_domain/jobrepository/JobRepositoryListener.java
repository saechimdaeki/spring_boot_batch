package com.example.spring_batch_domain.jobrepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobRepositoryListener implements JobExecutionListener {

    @Autowired
    private JobRepository jobRepository;

    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        String jobName = jobExecution.getJobInstance().getJobName();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("requestDate", "20210102").toJobParameters();

        JobExecution lastJobExecution = jobRepository.getLastJobExecution(jobName, jobParameters);
        if(lastJobExecution!=null){
            lastJobExecution.getStepExecutions().forEach(execution -> {
                BatchStatus status = execution.getStatus();
                ExitStatus exitStatus = execution.getExitStatus();
                log.info("status = {}", status);
                log.info("exitStatus = {}", exitStatus);
                String stepName = execution.getStepName();
                log.info("stepName = {}", stepName);
            });
        }
    }
}
