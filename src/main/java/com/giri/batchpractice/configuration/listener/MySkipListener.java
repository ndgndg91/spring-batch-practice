package com.giri.batchpractice.configuration.listener;

import com.giri.batchpractice.domain.Order;
import com.giri.batchpractice.domain.TrackedOrder;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.SkipListener;

@Log4j2
public class MySkipListener implements SkipListener<Order, TrackedOrder>{
    @Override
    public void onSkipInRead(Throwable t) {

    }

    @Override
    public void onSkipInWrite(TrackedOrder item, Throwable t) {

    }

    @Override
    public void onSkipInProcess(Order item, Throwable t) {
        log.info("Skipping processing of item with id : {}", item.getOrderId());
    }
}
