package com.giri.batchpractice.configuration.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class BillingJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job billingJob(){
        return jobBuilderFactory.get("billingJob").start(sendInvoiceStep()).build();
    }

    @Bean
    public Step sendInvoiceStep() {
        return stepBuilderFactory.get("sendInvoiceStep").tasklet(((contribution, chunkContext) -> {
            log.info("Invoice is sent to the customer.");
            return RepeatStatus.FINISHED;
        })).build();
    }

    /**
     * @return Step contains job
     */
    @Bean
    public Step nestedBillingJobStep(){
        return stepBuilderFactory.get("nestedBillingJobStep").job(billingJob()).build();
    }
}
