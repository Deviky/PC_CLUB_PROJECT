package com.pcclub.PC_Service.dto.PC;

import com.pcclub.PC_Service.dto.PCGroup.PCGroupDto;
import com.pcclub.PC_Service.dto.Reservation.ReserveDto;
import com.pcclub.PC_Service.enums.PCStatus;
import jakarta.validation.constraints.Null;
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
    @Null
    private List<ReserveDto> reservationList;
}
