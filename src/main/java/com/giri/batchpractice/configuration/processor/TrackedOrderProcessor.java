package com.giri.batchpractice.configuration.processor;

import com.giri.batchpractice.domain.Order;
import com.giri.batchpractice.domain.TrackedOrder;
import org.springframework.batch.item.ItemProcessor;

import java.util.UUID;

public class TrackedOrderProcessor implements ItemProcessor<Order, TrackedOrder> {

    @Override
    public TrackedOrder process(Order item) throws Exception {
        TrackedOrder trackedOrder = new TrackedOrder(item);
        trackedOrder.setTrackingNumber(UUID.randomUUID().toString());
        return trackedOrder;
    }
}
