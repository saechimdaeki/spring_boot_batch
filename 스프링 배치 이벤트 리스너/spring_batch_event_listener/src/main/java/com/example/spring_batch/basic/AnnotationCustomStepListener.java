package com.example.spring_batch.basic;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;

@Component
public class AnnotationCustomStepListener {

    @BeforeStep
    public void beforeStep(StepExecution stepExecution){
        System.out.println("@stepExecution.getStepName() : " + stepExecution.getStepName());
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution){
        System.out.println("@stepExecution.getStatus() : " + stepExecution.getStatus());
    }
}