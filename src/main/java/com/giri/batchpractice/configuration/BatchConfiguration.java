package com.giri.batchpractice.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job deliverPackageJob(){
        return jobBuilderFactory.get("deliverPackageJob")
                .start(packageItemStep())
                .next(driveToAddressStep())
                    .on("FAILED").to(storePackageStep())
                .from(driveToAddressStep())
                    .on("*").to(givePackageToCustomerStep())
                .end()
//                .next(givePackageToCustomerStep())
                .build();
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
