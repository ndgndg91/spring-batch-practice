package com.giri.batchpractice.configuration.rowmapper;

import com.giri.batchpractice.domain.Order;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderRowMapper implements RowMapper<Order> {

    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Order.builder()
                .orderId(rs.getLong("order_id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .email(rs.getString("email"))
                .cost(rs.getBigDecimal("cost"))
                .itemId(rs.getString("item_id"))
                .itemName(rs.getString("item_name"))
                .shipDate(rs.getTimestamp("ship_date").toLocalDateTime())
                .build();
    }
}
