package com.pcclub.PC_Service.dto.PCGroup;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PCGroupCreateRequest {
    @NotNull
    private String cpu;
    @NotNull
    private String gpu;
    @NotNull
    private int ram;
}
