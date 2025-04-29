package com.pcclub.PC_Service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PCGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @NotNull
    String cpu;
    @NotNull
    String gpu;
    @NotNull
    int ram;
    @OneToMany(mappedBy = "pcGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<PC> pcs;
    @OneToMany(mappedBy = "pcGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<ServiceDict> services;
}
