package com.example.JavaBank.controller;

import com.example.JavaBank.dto.*;
import com.example.JavaBank.exception.UserException;
import com.example.JavaBank.model.User;
import com.example.JavaBank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account management APIs")
public class UserController {
    @Autowired
    private UserService userService;

    private ResponseEntity<String> validateUserAccess(UserDetails user, String accountNumber) {
        try {
            User userDetails = userService.getUserByEmail(user.getUsername());
            if (!userDetails.getAccountNumber().equals(accountNumber)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You can only access your own account information.");
            }
            return null;
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found: " + e.getMessage());
        }
    }

    @Operation(summary = "Balance inquiry")
    @ApiResponse(responseCode = "200", description = "Http status 200 OK")
    @GetMapping("/balanceEnquire")
    public ResponseEntity<?> balance(@Valid @RequestBody EnquiryRequest request,
                                     @AuthenticationPrincipal UserDetails loggedInUser) {
        ResponseEntity<String> validationResponse = validateUserAccess(loggedInUser, request.getAccountNumber());
        if (validationResponse != null) return validationResponse;
        return ResponseEntity.ok(userService.balanceEnquire(request));
    }

    @GetMapping("/nameEnquire")
    public ResponseEntity<?> nameEnquire(@Valid @RequestBody EnquiryRequest request,
                                         @AuthenticationPrincipal UserDetails user) {
        ResponseEntity<String> validationResponse = validateUserAccess(user, request.getAccountNumber());
        if (validationResponse != null) return validationResponse;
        return ResponseEntity.ok(userService.nameEnquiry(request));
    }

    @PostMapping("/credit")
    public ResponseEntity<?> creditAccount(@Valid @RequestBody CreditDebitRequest request,
                                           @AuthenticationPrincipal UserDetails user) {
        ResponseEntity<String> validationResponse = validateUserAccess(user, request.getAccountNumber());
        if (validationResponse != null) return validationResponse;
        return ResponseEntity.ok(userService.creditAccount(request));
    }

    @PostMapping("/debit")
    public ResponseEntity<?> debitAccount(@Valid @RequestBody CreditDebitRequest request,
                                          @AuthenticationPrincipal UserDetails user) {
        ResponseEntity<String> validationResponse = validateUserAccess(user, request.getAccountNumber());
        if (validationResponse != null) return validationResponse;
        return ResponseEntity.ok(userService.debitAccount(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody Transfer transfer,
                                      @AuthenticationPrincipal UserDetails user) {
        ResponseEntity<String> validationResponse = validateUserAccess(user, transfer.getSourceAccountNumber());
        if (validationResponse != null) return validationResponse;
        return ResponseEntity.ok(userService.transfer(transfer));
    }
}
