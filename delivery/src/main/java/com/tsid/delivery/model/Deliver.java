package com.tsid.delivery.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "delivers")
public class Deliver {
    @Id
    @Column(name = "deliver_id", nullable = false, unique = true)
    @JsonProperty("id")
    private Long id;

    @Column(name = "deliver_name", nullable = false)
    private String name;

    @Column(name = "deliver_contact", nullable = false)
    private String contact;

    @Column(name = "deliver_busy", nullable = false)
    private boolean busy = false;

    @CreationTimestamp
    @Column(name = "deliver_create_date", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm")
    private Timestamp created;

    @UpdateTimestamp
    @Column(name = "deliver_update_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm")
    private Timestamp updated;
}
