package com.giri.batchpractice.configuration.job;

import com.giri.batchpractice.configuration.processor.TrackedOrderProcessor;
import com.giri.batchpractice.configuration.rowmapper.OrderRowMapper;
import com.giri.batchpractice.domain.Order;
import com.giri.batchpractice.domain.TrackedOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class JdbcCursorJobConfiguration {

    private static final String ORDER_SQL = "SELECT order_id, first_name, last_name, email, cost, item_id, item_name, ship_date " +
            "FROM SHIPPED_ORDER " +
            "ORDER BY order_id";

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Bean
    public Job jdbcCursorJob(){
        return jobBuilderFactory.get("jdbcCursorJob").start(jdbcCursorStep()).build();
    }

    @Bean
    public Step jdbcCursorStep(){
        return stepBuilderFactory.get("jdbcCursorStep")
                .<Order, TrackedOrder>chunk(3)
                .reader(jdbcCursorOrderItemReader())
//                .processor(orderValidatingProcessor())
                .processor(trackedOrderItemProcessor())
                .writer(trackedOrderToJsonFileWriter())
                .build();
    }

    @Bean
    public ItemProcessor<Order, TrackedOrder> trackedOrderItemProcessor() {
        return new TrackedOrderProcessor();
    }

    @Bean
    public ItemProcessor<Order, Order> orderValidatingProcessor() {
        BeanValidatingItemProcessor<Order> itemProcessor = new BeanValidatingItemProcessor<>();
        itemProcessor.setFilter(true);
        return itemProcessor;
    }

    @Bean
    public ItemReader<Order> jdbcCursorOrderItemReader(){
        return new JdbcCursorItemReaderBuilder<Order>() // not thread safe, so it is possible in single thread
                .dataSource(dataSource)
                .name("jdbcCursorOrderItemReader")
                .sql(ORDER_SQL)
                .rowMapper(new OrderRowMapper())
                .build();
    }

    @Bean
    public ItemWriter<TrackedOrder> trackedOrderToJsonFileWriter(){
        return new JsonFileItemWriterBuilder<TrackedOrder>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(new FileSystemResource("src/main/resources/data/trackedOrder.csv"))
                .name("trackedOrderToJsonFileWriter")
                .build();
    }


}
