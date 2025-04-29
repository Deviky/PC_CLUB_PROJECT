package com.pcclub.PC_Service.models;


import com.pcclub.PC_Service.enums.PCStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceDict {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @NotNull
    String name;
    @NotNull
    String description;
    @NotNull
    @Column(name="price_per_hour")
    float pricePerHour;
    @Null
    @Column(name="min_age")
    int minAge;
    @Null
    @Column(name="max_age")
    int maxAge;
    @NotNull
    @Column(name="min_time")
    LocalTime minTime;
    @NotNull
    @Column(name="max_time")
    LocalTime maxTime;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "pc_group", nullable = false)
    private PCGroup pcGroup;
}
