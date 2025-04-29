package com.pcclub.PC_Service.dto.PC;

import com.pcclub.PC_Service.enums.PCStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PCCreateResponse {
    private Long id;
    private String message;
}

