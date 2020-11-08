package com.giri.batchpractice.configuration.fieldsetmapper;

import com.giri.batchpractice.domain.Order;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class OrderFieldSetMapper implements FieldSetMapper<Order> {
    @Override
    public Order mapFieldSet(FieldSet fieldSet) throws BindException {
        Date sd = fieldSet.readDate("ship_date");
        LocalDateTime shipDate = sd.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Order.builder()
                .orderId(fieldSet.readLong("order_id"))
                .firstName(fieldSet.readString("first_name"))
                .lastName(fieldSet.readString("last_name"))
                .email(fieldSet.readString("email"))
                .cost(fieldSet.readBigDecimal("cost"))
                .itemId(fieldSet.readString("item_id"))
                .itemName(fieldSet.readString("item_name"))
                .shipDate(shipDate)
                .build();
    }
}
