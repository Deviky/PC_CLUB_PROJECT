package com.pcclub.PC_Service.models;

import com.pcclub.PC_Service.enums.PCStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PC {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @NotNull
    @Enumerated(EnumType.STRING)
    PCStatus status;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "pc_group", nullable = false)
    private PCGroup pcGroup;
}
