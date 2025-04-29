package com.pcclub.Payment_Service.models;

import com.pcclub.Payment_Service.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column(name = "client_id")
    @NotNull
    Long clientId;
    @Column(name = "service_order_id")
    Long serviceOrderId;
    @Column(name = "amount_of_cash_payment")
    @NotNull
    Float amountOfCashPayment;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    PaymentStatus paymentStatus;
    @NotNull
    @Column(name = "useBonus")
    Boolean useBonus;
    @Column(name = "operation_dttm")
    @NotNull
    Date operationDttm;
}
