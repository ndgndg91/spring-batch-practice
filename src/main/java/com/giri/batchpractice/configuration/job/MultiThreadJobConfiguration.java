package com.giri.batchpractice.configuration.job;

import com.giri.batchpractice.configuration.exception.OrderProcessingException;
import com.giri.batchpractice.configuration.listener.MyRetryListener;
import com.giri.batchpractice.configuration.processor.FreeShippingOrderProcessor;
import com.giri.batchpractice.configuration.processor.SkipTrackedOrderProcessor;
import com.giri.batchpractice.configuration.rowmapper.OrderRowMapper;
import com.giri.batchpractice.domain.Order;
import com.giri.batchpractice.domain.TrackedOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class MultiThreadJobConfiguration {

    private static final String INSERT_TRACKED_ORDER = "INSERT INTO TRACKED_ORDER " +
            "(order_id, first_name, last_name, email, cost, item_id, item_name, ship_date, tracking_number, free_shipping) " +
            "VALUES (:orderId, :firstName, :lastName, :email, :cost, :itemId, :itemName, :shipDate, :trackingNumber, :freeShipping)";

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Bean
    public ItemProcessor<Order, Order> validatingProcessor() {
        BeanValidatingItemProcessor<Order> orderValidatingProcessor = new BeanValidatingItemProcessor<>();
        orderValidatingProcessor.setFilter(true);
        return orderValidatingProcessor;
    }

    @Bean
    public ItemProcessor<Order, TrackedOrder> skipProcessor() {
        return new SkipTrackedOrderProcessor();
    }

    @Bean
    public ItemProcessor<TrackedOrder, TrackedOrder> onlyFreeShippingProcessor() {
        return new FreeShippingOrderProcessor();
    }

    @Bean
    public PagingQueryProvider orderPagingQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
        factory.setSelectClause("SELECT order_id, first_name, last_name, email, cost, item_id, item_name, ship_date ");
        factory.setFromClause("FROM SHIPPED_ORDER");
        factory.setSortKey("order_id");
        factory.setDataSource(dataSource);

        return factory.getObject();
    }

    @Bean
    public ItemReader<Order> orderPagingItemReader() throws Exception {
        return new JdbcPagingItemReaderBuilder<Order>()
                .dataSource(dataSource)
                .name("orderPagingItemReader")
                .queryProvider(orderPagingQueryProvider())
                .pageSize(10)
                .rowMapper(new OrderRowMapper())
                .saveState(false)
                .build();
    }

    @Bean
    public ItemProcessor<Order, TrackedOrder> processorForMultiThread() {
        return new CompositeItemProcessorBuilder<Order, TrackedOrder>()
                .delegates(validatingProcessor(), skipProcessor(), onlyFreeShippingProcessor())
                .build();
    }

    @Bean
    public ItemWriter<TrackedOrder> trackedOrderItemWriter() {
        return new JdbcBatchItemWriterBuilder<TrackedOrder>()
                .dataSource(dataSource)
                .sql(INSERT_TRACKED_ORDER)
                .beanMapped()
                .build();
    }

    @Bean
    public ThreadPoolTaskExecutor multiThreadJobExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        return executor;
    }

    @Bean
    public JobExecutionListener jobExecutionListener(ThreadPoolTaskExecutor multiThreadJobExecutor) {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("multiThreadJob 을 시작하겠습니다.");
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                log.info("multiThreadJob 을 끝났습니다.");
                // job 이 종료되면 해당 executor 도 종료한다.
                multiThreadJobExecutor.shutdown();
            }
        };
    }

    @Bean
    public Job multiThreadJob() throws Exception {
        return jobBuilderFactory.get("multiThreadJob")
                .listener(jobExecutionListener(multiThreadJobExecutor()))
                .start(multiThreadStep()).build();
    }

    @Bean
    public Step multiThreadStep() throws Exception {
        return stepBuilderFactory.get("multiThreadStep")
                .<Order, TrackedOrder>chunk(10)
                .reader(orderPagingItemReader())
                .processor(processorForMultiThread())
                .faultTolerant()
                .retry(OrderProcessingException.class)
                .retryLimit(3)
                .listener(new MyRetryListener())
                .writer(trackedOrderItemWriter())
                .taskExecutor(multiThreadJobExecutor())
                .build();
    }
}

