package com.giri.batchpractice.configuration.job;

import com.giri.batchpractice.configuration.rowmapper.OrderRowMapper;
import com.giri.batchpractice.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class ReadDatabaseByJdbcCursorJobConfiguration {

    private static final String ORDER_SQL = "SELECT order_id, first_name, last_name, email, cost, item_id, item_name, ship_date " +
            "FROM SHIPPED_ORDER " +
            "ORDER BY order_id";

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Bean
    public Job readDatabaseByJdbcCursorJob(){
        return jobBuilderFactory.get("readDatabaseByJdbcCursorJob").start(readDatabaseByJdbcCursorStep()).build();
    }

    @Bean
    public Step readDatabaseByJdbcCursorStep(){
        return stepBuilderFactory.get("readDatabaseByJdbcCursorStep")
                .<Order, Order>chunk(3)
                .reader(readDatabaseByJdbcCursorOrderItemReader())
                .writer(items -> {
                    log.info("Received list size {}", items.size());
                    items.forEach(log::info);
                })
                .build();
    }

    @Bean
    public ItemReader<Order> readDatabaseByJdbcCursorOrderItemReader(){
        return new JdbcCursorItemReaderBuilder<Order>() // not thread safe, so it is possible in single thread
                .dataSource(dataSource)
                .name("jdbcCursorItemReader")
                .sql(ORDER_SQL)
                .rowMapper(new OrderRowMapper())
                .build();
    }


}
