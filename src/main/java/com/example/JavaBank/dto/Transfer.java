package com.example.JavaBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transfer {
    private String sourceAccountNumber;
    private String destinationAccount;
    private BigDecimal amount;
}
