package com.giri.batchpractice.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    private Long orderId;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal cost;
    private String itemId;
    private String itemName;
    private LocalDateTime shipDate;
}
