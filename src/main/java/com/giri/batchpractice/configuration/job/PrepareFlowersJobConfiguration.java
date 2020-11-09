package com.giri.batchpractice.configuration.job;

import com.giri.batchpractice.configuration.listener.FlowersSelectionStepExecutionListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class PrepareFlowersJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final Flow deliveryFlow;

    @Bean
    public Job prepareFlowersJob(){
        return this.jobBuilderFactory.get("prepareFlowersJob")
                .start(selectFlowersStep())
                    .on("TRIM REQUIRED").to(removeThornsStep()).next(arrangeFlowersStep())
                .from(selectFlowersStep())
                    .on("NO TRIM REQUIRED").to(arrangeFlowersStep())
                .from(arrangeFlowersStep()).on("*").to(deliveryFlow)
                .end()
                .build();
    }


    @Bean
    public Step selectFlowersStep() {
        return stepBuilderFactory.get("selectFlowersStep").tasklet(((contribution, chunkContext) -> {
            log.info("Selecting flowers for order.");
            return RepeatStatus.FINISHED;
        })).listener(selectFlowerListener()).build();
    }

    @Bean
    public StepExecutionListener selectFlowerListener(){
        return new FlowersSelectionStepExecutionListener();
    }

    @Bean
    public Step arrangeFlowersStep() {
        return stepBuilderFactory.get("arrangFlowersStep").tasklet(((contribution, chunkContext) -> {
            log.info("Arranging flowers for order.");
            return RepeatStatus.FINISHED;
        })).build();
    }

    @Bean
    public Step removeThornsStep() {
        return stepBuilderFactory.get("removeThornsStep").tasklet(((contribution, chunkContext) -> {
            log.info("Removing thorns from roses.");
            return RepeatStatus.FINISHED;
        })).build();
    }

}
