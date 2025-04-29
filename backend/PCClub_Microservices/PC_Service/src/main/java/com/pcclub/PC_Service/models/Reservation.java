package com.pcclub.PC_Service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "reservations")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pc_id")
    private Long pcId;

    @Column(name = "reserved_from")
    private Date reservedFrom;

    @Column(name = "reserved_to")
    private Date reservedTo;
}
