package com.giri.batchpractice.configuration.job;

import com.giri.batchpractice.configuration.decider.DeliveryDecider;
import com.giri.batchpractice.configuration.decider.ReceiptDecider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class DeliverPackageJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final Step nestedBillingJobStep;

    private final Flow billingFlow;

    @Bean
    public Flow deliveryFlow(){
        return new FlowBuilder<SimpleFlow>("deliveryFlow")
                .start(driveToAddressStep())
                    .on("FAILED").to(storePackageStep())
//                    .on("FAILED").stop() // Batch Status STOPPED
//                    .on("FAILED").fail() // Batch Status FAILED
                .from(driveToAddressStep())
                    .on("*").to(decider())
                        .on("PRESENT").to(givePackageToCustomerStep())
                            .next(receiptDecider()).on("CORRECT").to(thankCustomerStep())
                            .from(receiptDecider()).on("INCORRECT").to(refundStep())
                    .from(decider())
                            .on("NOT PRESENT").to(leaveAtDoorStep())
                .end();
    }

    @Bean
    public Job deliverPackageJob(){
        return jobBuilderFactory.get("deliverPackageJob")
                .start(packageItemStep())
                // parallel flows
                .split(new SimpleAsyncTaskExecutor())
                .add(deliveryFlow(), billingFlow)
//                .on("*").to(deliveryFlow())
//                .next(nestedBillingJobStep) // this step contains billingJob
                .end()
                .build();
    }

    @Bean
    public JobExecutionDecider receiptDecider(){
        return new ReceiptDecider();
    }

    @Bean
    public Step thankCustomerStep(){
        return stepBuilderFactory.get("thankCustomerStep")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("Thanking the customer.");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step refundStep(){
        return stepBuilderFactory.get("refundStep")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("Refund customer money");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public JobExecutionDecider decider(){
        return new DeliveryDecider();
    }

    @Bean
    public Step leaveAtDoorStep(){
        return stepBuilderFactory.get("leaveAtDoorStep")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("Leaving the package at the door.");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step storePackageStep(){
        return stepBuilderFactory.get("storePackageStep")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("Storing the package while the customer address located.");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step packageItemStep(){
        return stepBuilderFactory.get("packageItemStep")
                .tasklet(((contribution, chunkContext) -> {
                    //    java -jar demo-0.0.1.jar "item=job-parameter-test" "run.date(date)=2020/11/01"
                    log.info("packageItemStep execute!");
                    String item = chunkContext.getStepContext()
                            .getJobParameters().getOrDefault("item", "").toString();
                    String runDate = chunkContext.getStepContext()
                            .getJobParameters().getOrDefault("run.date", "").toString();

                    log.info("run date : {}, item parameter : {}", runDate, item);
                    // run date : Sun Nov 01 00:00:00 KST 2020, item parameter : job-parameter-test
                    for (int i = 0; i < 900; i++) {
                        Thread.sleep(1000);
                        log.info("{} second!", i+1);
                    }

                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step driveToAddressStep(){
        return stepBuilderFactory.get("driveToAddress")
                .tasklet(((contribution, chunkContext) -> {

                    // Job 이 실패하더라도, 다시 실행했을 때 실패한 Step 부터 실행되는지 테스트
                    // Conditional Flow 테스트
//                    if (true) throw new IllegalStateException("운전하다가 길을 잃어버림.");
                    log.info("Successfully arrived at the address!");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step givePackageToCustomerStep(){
        return this.stepBuilderFactory.get("givePackageToCustomerStep")
                .tasklet((contribution, chunkContext) -> {

                    log.info("Given the package to customer.");
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
