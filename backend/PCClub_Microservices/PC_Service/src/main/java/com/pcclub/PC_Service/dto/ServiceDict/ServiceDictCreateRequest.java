package com.pcclub.PC_Service.dto.ServiceDict;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDictCreateRequest {
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private float pricePerHour;
    @NotNull
    private Integer minAge;
    @NotNull
    private Integer maxAge;
    @NotNull
    private LocalTime minTime;
    @NotNull
    private LocalTime maxTime;
    @NotNull
    private Long pcGroupId;
}
