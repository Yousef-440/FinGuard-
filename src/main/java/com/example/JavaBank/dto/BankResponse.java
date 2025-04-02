package com.example.JavaBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankResponse {
    private String responseCode;
    private String ResponseMessage;
    private AccountInfo accountInfo;

}
