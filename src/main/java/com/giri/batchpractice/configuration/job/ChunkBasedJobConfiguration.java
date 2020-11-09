package com.giri.batchpractice.configuration.job;

import com.giri.batchpractice.configuration.reader.SimpleItemReader;
import com.giri.batchpractice.configuration.writer.SimpleItemWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class ChunkBasedJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job chunkBasedJob(){
        return jobBuilderFactory.get("chunkBasedJob").start(chunkBasedStep()).build();
    }

    @Bean
    public Step chunkBasedStep(){
        return stepBuilderFactory.get("chunkBasedStep")
                .<String, String>chunk(3)
                .reader(simpleItemReader())
                .writer(simpleItemWriter())
                .build();
    }

    @Bean
    public ItemReader<String> simpleItemReader() {
        return new SimpleItemReader();
    }

    @Bean
    public ItemWriter<String> simpleItemWriter(){
        return new SimpleItemWriter();
    }
}
