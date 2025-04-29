package com.PCCLub.Order_Service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReserveDto {
    Long pcId;
    @NotNull
    private Date reservedFrom;
    @NotNull
    private Date reservedTo;
}