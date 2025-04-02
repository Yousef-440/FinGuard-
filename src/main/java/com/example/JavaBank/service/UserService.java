package com.example.JavaBank.service;

import com.example.JavaBank.dto.*;
import com.example.JavaBank.model.User;

public interface UserService {
    BankResponse createAccount(UserDto userDto);

    BankResponse balanceEnquire(EnquiryRequest enquiryRequest);

    String nameEnquiry(EnquiryRequest enquiryRequest);

    BankResponse creditAccount(CreditDebitRequest debitRequest);

    BankResponse debitAccount(CreditDebitRequest request);

    BankResponse transfer(Transfer transfer);

    User getUserByEmail(String email);
}
