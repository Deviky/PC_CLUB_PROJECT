package com.PCCLub.Order_Service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Order_m")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    Long clientId;
    Long serviceId;
    @Column(name = "pc_id")
    Long pcId;
    @Column(name = "total_service_count")
    Integer totalServiceCount;
    @Column(name = "start_dttm")
    Date startDttm;
    @Column(name = "end_dttm")
    Date endDttm;
}
