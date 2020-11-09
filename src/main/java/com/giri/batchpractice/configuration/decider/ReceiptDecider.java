package com.giri.batchpractice.configuration.decider;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import java.util.Random;

@Log4j2
public class ReceiptDecider implements JobExecutionDecider {

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

        String exitCode = new Random().nextFloat() < .70f ? "CORRECT" : "INCORRECT";
        log.info("The item delivered is {}", exitCode);

        return new FlowExecutionStatus(exitCode);
    }
}
