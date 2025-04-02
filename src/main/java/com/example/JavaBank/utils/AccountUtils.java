package com.example.JavaBank.utils;

import java.time.Year;

public class AccountUtils {
    public static final String ACCOUNT_CREATION_CODE = "002";
    public static final String ACCOUNT_CREATION_MESSAGE ="Account has been successfully created";

    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_message = "Account found successfully";

    public static final String ACCOUNT_CREDITED_CODE = "005";
    public static final String ACCOUNT_CREDITED_message = "Account found successfully";

    public static final String ACCOUNT_DEBITED = "006";
    public static final String ACCOUNT_DEBITED_MESSAGE = "The amount has been debited from the account successfully.";

    public static final String TRANSFER_SUCCESS = "007";
    public static final String TRANSFER_SUCCESS_MESSAGE = "Transfer Has been successfully.";

    public static String generateAccountNumber(){
        Year currentYear = Year.now();//2025
        int min = 10000;
        int max = 99999;

        int randomNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);

        String year = String.valueOf(currentYear);
        String randomNum = String.valueOf(randomNumber);
        StringBuilder accountNumber = new StringBuilder();

        return accountNumber.append(year).append(randomNum).toString();
    }
}
