package com.giri.batchpractice.configuration.processor;

import com.giri.batchpractice.domain.TrackedOrder;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;

public class FreeShippingOrderProcessor implements ItemProcessor<TrackedOrder, TrackedOrder> {
    @Override
    public TrackedOrder process(TrackedOrder item) throws Exception {
        if (item.getCost().compareTo(new BigDecimal("80")) > 0) {
             item.setFreeShipping(true);
        }

        return item.isFreeShipping() ? item : null;
    }
}
