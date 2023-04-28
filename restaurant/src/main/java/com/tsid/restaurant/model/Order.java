package com.tsid.restaurant.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "order_id", nullable = false, unique = true)
    @JsonProperty("id")
    private Long id;

    @Column(name = "order_address", nullable = false, unique = true)
    private String address;

    @Column(name = "order_phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "order_dishes")
    private String dishList;

    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus = OrderStatus.CREATED;

    @CreationTimestamp
    @Column(name = "order_create_date", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm")
    private Timestamp created;

    @UpdateTimestamp
    @Column(name = "order_update_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm")
    private Timestamp updated;

    public static String getJsonRef(@NonNull List<String> list) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(list);
    }

    public static List<String> getValueFromJson(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, new TypeReference<>() {
        });
    }
}
