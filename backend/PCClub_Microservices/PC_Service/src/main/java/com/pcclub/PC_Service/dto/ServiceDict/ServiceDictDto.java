package com.pcclub.PC_Service.dto.ServiceDict;

import com.pcclub.PC_Service.dto.PCGroup.PCGroupDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDictDto {
    private Long id;
    private String name;
    private String description;
    private float pricePerHour;
    private int minAge;
    private int maxAge;
    private LocalTime minTime;
    private LocalTime maxTime;
    private PCGroupDto pcGroup;
    private Long pcGroupId;
}
