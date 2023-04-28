package com.tsid.kitchen.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "dishes")
public class Dish {
    @Id
    @Column(name = "dish_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "dish_name", nullable = false, unique = true)
    private String name;

    @Column(name = "dish_recipe", nullable = false, unique = true)
    private String recipe;

    @Column(name = "dish_available", nullable = false)
    private Boolean available = true;

    @CreationTimestamp
    @Column(name = "dish_create_date", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm")
    private Timestamp created;

    @UpdateTimestamp
    @Column(name = "dish_update_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm")
    private Timestamp updated;
}
