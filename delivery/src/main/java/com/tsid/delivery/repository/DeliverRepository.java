package com.tsid.delivery.repository;

import com.tsid.delivery.model.Deliver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface DeliverRepository extends JpaRepository<Deliver, Long> {
    Set<Deliver> findAllByBusyFalse();
}
