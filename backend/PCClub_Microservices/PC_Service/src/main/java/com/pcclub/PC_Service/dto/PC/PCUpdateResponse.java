package com.pcclub.PC_Service.dto.PC;

import com.pcclub.PC_Service.enums.PCStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PCUpdateResponse {
    private Long id;
    private PCStatus status;
    private Long pcGroupId;
}
