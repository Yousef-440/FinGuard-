package com.example.JavaBank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class Handle {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorObj> handleUserEx(UserException ex){
        ErrorObj errorObj = new ErrorObj();
        errorObj.setStatusCode(HttpStatus.CONFLICT.value());
        errorObj.setMessage(ex.getMessage());
        errorObj.setDate(new Date());

        return new ResponseEntity<>(errorObj, HttpStatus.CONFLICT);
    }
}
