package com.giri.batchpractice.domain;

import lombok.*;
import org.springframework.beans.BeanUtils;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
public class TrackedOrder extends Order {

    private String trackingNumber;

    private boolean freeShipping;

    public TrackedOrder(Order order) {
        BeanUtils.copyProperties(order, this);
    }
}
