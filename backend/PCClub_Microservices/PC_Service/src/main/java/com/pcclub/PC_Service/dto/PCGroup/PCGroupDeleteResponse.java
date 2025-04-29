package com.pcclub.PC_Service.dto.PCGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PCGroupDeleteResponse {
    private Long id;
    private String message;
}
