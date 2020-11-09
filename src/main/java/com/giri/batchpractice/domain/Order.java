package com.giri.batchpractice.domain;

import lombok.*;

import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    private Long orderId;
    private String firstName;
    private String lastName;
    @Pattern(regexp = ".*\\.gov")
    private String email;
    private BigDecimal cost;
    private String itemId;
    private String itemName;
    private LocalDateTime shipDate;
}
