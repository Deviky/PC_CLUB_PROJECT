package com.PCCLub.Order_Service.dto;

import com.PCCLub.Order_Service.enums.PCStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PCDto {
    private Long id;
    private PCStatus status;
    private PCGroupDto pcGroup;
    private Long pcGroupId;
    private List<ReserveDto> reservationList;
}
