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
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class ReadDatabaseByJdbcPagingJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Bean
    public Job readDatabaseByJdbcPagingJob() throws Exception {
        return jobBuilderFactory.get("readDatabaseByJdbcPagingJob").start(readDatabaseByJdbcPagingStep()).build();
    }

    @Bean
    public Step readDatabaseByJdbcPagingStep() throws Exception {
        // chunk size 10
        return stepBuilderFactory.get("readDatabaseByJdbcPagingStep")
                .<Order, Order>chunk(10)
                .reader(readDatabaseByJdbcPagingOrderItemReader())
                .writer(items -> {
                    log.info("Received list size {}", items.size());
                    items.forEach(log::info);
                })
                .build();
    }

    @Bean
    public ItemReader<Order> readDatabaseByJdbcPagingOrderItemReader() throws Exception {
        // page size 10
        return new JdbcPagingItemReaderBuilder<Order>() // thread safe, so it is possible in multi thread
                .dataSource(dataSource)
                .name("jdbcPagingItemReader")
                .queryProvider(queryProvider())
                .pageSize(10)
                .rowMapper(new OrderRowMapper())
                .build();
    }

    @Bean
    public  PagingQueryProvider queryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
        factory.setSelectClause("SELECT order_id, first_name, last_name, email, cost, item_id, item_name, ship_date ");
        factory.setFromClause("FROM SHIPPED_ORDER");
        factory.setSortKey("order_id");
        factory.setDataSource(dataSource);
        return factory.getObject();
    }
}
