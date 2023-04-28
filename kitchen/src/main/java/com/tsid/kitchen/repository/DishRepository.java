package com.tsid.kitchen.repository;

import com.tsid.kitchen.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DishRepository extends JpaRepository<Dish, Long> {
    List<Dish> findAllByAvailableTrue();

    Optional<Dish> findByName(String name);
}
