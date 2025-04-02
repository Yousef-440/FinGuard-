package com.example.JavaBank.controller;

import com.example.JavaBank.exception.UserException;
import com.example.JavaBank.model.Transaction;
import com.example.JavaBank.model.User;
import com.example.JavaBank.service.UserService;
import com.example.JavaBank.service.impl.BankStatement;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private BankStatement bankStatement;
    @Autowired
    private UserService userService;

    @GetMapping("/statement")
    public List<Transaction> generateBankStatement(@RequestParam String accountNumber,
                                                   @RequestParam String startDate , @RequestParam String endDate
    , @AuthenticationPrincipal UserDetails userDetails) throws DocumentException, FileNotFoundException {
        User user = userService.getUserByEmail(userDetails.getUsername());
        if(!user.getAccountNumber().equals(accountNumber)){
            throw new UserException("You can only access your own account information.");
        }
        return bankStatement.transactions(accountNumber , startDate , endDate);
    }
}
