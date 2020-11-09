package com.giri.batchpractice.configuration.processor;

import com.giri.batchpractice.configuration.exception.OrderProcessingException;
import com.giri.batchpractice.domain.Order;
import com.giri.batchpractice.domain.TrackedOrder;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemProcessor;

import java.util.UUID;

@Log4j2
public class SkipTrackedOrderProcessor implements ItemProcessor<Order, TrackedOrder> {
    @Override
    public TrackedOrder process(Order item) throws Exception {
        log.info("processing of item with id : {}", item.getOrderId());
        TrackedOrder trackedOrder = new TrackedOrder(item);
        trackedOrder.setTrackingNumber(getTrackingNumber());
        return trackedOrder;
    }

    private String getTrackingNumber() {

        if (Math.random() < .10) {
            throw  new OrderProcessingException("의도적으로 skip 하기 위한 예외");
        }

        return UUID.randomUUID().toString();
    }
}
