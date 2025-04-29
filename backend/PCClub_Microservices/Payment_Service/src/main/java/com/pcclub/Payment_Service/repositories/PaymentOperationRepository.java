package com.pcclub.Payment_Service.repositories;

import com.pcclub.Payment_Service.models.PaymentOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentOperationRepository extends JpaRepository<PaymentOperation, Long> {
    List<PaymentOperation> findAllByClientIdOrderByOperationDttmDesc(Long clientId); // Сортировка по дате от самой свежей
    Optional<PaymentOperation> findByServiceOrderId(Long serviceOrderId);
}
