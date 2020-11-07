package com.giri.batchpractice.configuration.listener;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.util.Optional;

@Log4j2
public class FlowersSelectionStepExecutionListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("Executing before Select Flowers Step.");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Executing after Select Flowers Step.");
        String flowerType = stepExecution.getJobParameters().getString("type");
        return Optional.ofNullable(flowerType)
                .map(ft -> ft.equalsIgnoreCase("roses") ?
                        new ExitStatus("TRIM REQUIRED") : new ExitStatus("NO TRIM REQUIRED"))
                .orElseThrow();
    }
}
