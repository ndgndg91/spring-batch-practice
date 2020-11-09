package com.giri.batchpractice.configuration.decider;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import java.time.LocalDateTime;

@Log4j2
public class DeliveryDecider implements JobExecutionDecider {

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        String result = LocalDateTime.now().getHour() > 12 ? "PRESENT" : "NOT PRESENT";
        log.info("Decider result is {}", result);
        return new FlowExecutionStatus(result); // custom exit status
    }
}
