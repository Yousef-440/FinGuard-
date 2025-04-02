package com.example.JavaBank.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorObj {
    private int statusCode;
    private String message;
    private Date date;
}
