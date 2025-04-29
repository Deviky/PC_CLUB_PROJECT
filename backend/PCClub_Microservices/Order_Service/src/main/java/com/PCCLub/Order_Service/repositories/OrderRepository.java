package com.PCCLub.Order_Service.repositories;

import com.PCCLub.Order_Service.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByClientIdOrderByStartDttm(Long clientId);
    List<Order> findAllByPcId(Long pcId);
}
