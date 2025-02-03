package com.grewal.orderservice.repository;

import com.grewal.orderservice.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {

    Optional<List<Order>> findAllByApplicationId(String application, Pageable pageable);

   }
