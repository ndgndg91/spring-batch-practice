package com.giri.batchpractice.configuration.job;

import com.giri.batchpractice.configuration.fieldsetmapper.OrderFieldSetMapper;
import com.giri.batchpractice.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class ReadCsvJobConfiguration {

    private static final String[] tokens = {"order_id","first_name","last_name","email","cost","item_id","item_name","ship_date"};

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job csvFileChunkBasedJob(){
        return jobBuilderFactory.get("csvFileChunkBasedJob").start(csvFileChunkBasedStep()).build();
    }

    @Bean
    public Step csvFileChunkBasedStep() {
        return stepBuilderFactory.get("csvFileChunkBasedStep")
                .<Order, Order>chunk(3)
                .reader(orderItemReader())
                .writer(items -> {
                    log.info("Received list of size {}", items.size());
                    items.forEach(log::info);
                })
                .build();
    }

    @Bean
    public ItemReader<Order> orderItemReader(){
        FlatFileItemReader<Order> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(1);
        itemReader.setResource(new ClassPathResource("data/shipped_orders.csv"));

        DefaultLineMapper<Order> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(tokens);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new OrderFieldSetMapper());

        itemReader.setLineMapper(lineMapper);
        return itemReader;
    }
}
