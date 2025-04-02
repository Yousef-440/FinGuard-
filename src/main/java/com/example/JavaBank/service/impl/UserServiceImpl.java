package com.example.JavaBank.service.impl;

import com.example.JavaBank.Enum.TransactionType;
import com.example.JavaBank.dto.*;
import com.example.JavaBank.exception.UserException;
import com.example.JavaBank.model.Transaction;
import com.example.JavaBank.model.User;
import com.example.JavaBank.repo.TransactionRepository;
import com.example.JavaBank.repo.UserRepository;
import com.example.JavaBank.service.EmailService;
import com.example.JavaBank.service.UserService;
import com.example.JavaBank.utils.AccountUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UserServiceImpl implements UserService {

    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    String formattedDateTime = now.format(formatter);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public BankResponse createAccount(UserDto userDto) {

        String email = userDto.getEmail().trim();
        String phoneNumber1 = userDto.getPhoneNumber().trim();
        String phoneNumber2 = userDto.getAlternativePhoneNumber().trim();


        if(userRepository.existsByEmail(userDto.getEmail())){
            throw new UserException("Sorry, User already exists");
        }

        if (phoneNumber1.equals(phoneNumber2)) {
            throw new UserException("Different phone numbers must be entered.");
        }

        User user = User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .otherName(userDto.getOtherName())
                .gender(userDto.getGender())
                .address(userDto.getAddress())
                .stateOfOrigin(userDto.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(email)
                .password(passwordEncoder.encode(userDto.getPassword()))
                .phoneNumber(phoneNumber1)
                .alternativePhoneNumber(phoneNumber2)
                .status("ACTIVE")
                .build();


        User newUser = userRepository.save(user);

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(newUser.getEmail())
                .subject("Account Creation")
                .messageBody("congratulations! You Account Has been successfully created \nYour Account Details:\n" +
                     "Account Name: " + newUser.getFirstName() + " " +newUser.getLastName() + " " + newUser.getOtherName()+
                "\nAccount Number: " + newUser.getAccountNumber())

                .build();
        emailService.sendEmailAlert(emailDetails);

        AccountInfo accountInfo = AccountInfo.builder()
                .accountName(newUser.getFirstName() + " " + (newUser.getOtherName().isEmpty() ? "" : newUser.getOtherName()))
                .accountNumber(newUser.getAccountNumber())
                .accountBalance(newUser.getAccountBalance())
                .build();

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_CODE)
                .ResponseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(accountInfo)
                .build();
    }

    @Override
    public BankResponse balanceEnquire(EnquiryRequest enquiryRequest) {
        if(!userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())){
            throw new UserException("Sorry, the account number does not exist.");
        }
        User user = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .ResponseMessage(AccountUtils.ACCOUNT_FOUND_message)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(user.getAccountBalance())
                        .accountNumber(enquiryRequest.getAccountNumber())
                        .accountName(user.getFirstName() + " " + user.getOtherName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        if(!userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())){
            throw new UserException("Sorry, the account number does not exist.");
        }
        User user = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();
    }

    @Override
    @Transactional
    public BankResponse creditAccount(CreditDebitRequest debitRequest) {
        if(!userRepository.existsByAccountNumber(debitRequest.getAccountNumber())){
            throw new UserException("Sorry, the account number does not exist.");
        }

        User user = userRepository.findByAccountNumber(debitRequest.getAccountNumber());

        BigDecimal amount = debitRequest.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserException("Invalid transaction amount.");
        }

        user.setAccountBalance(user.getAccountBalance().add(debitRequest.getAmount()));

        userRepository.save(user);


        Transaction transaction = new Transaction();
        transaction.setAccountNumber(debitRequest.getAccountNumber());
        transaction.setAmount(debitRequest.getAmount());
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setUser(user);
        transactionRepository.save(transaction);


        EmailDetails details = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("Deposit Confirmation")
                .messageBody("Dear " +user.getFirstName()+"\n"+
                    "We are pleased to inform you that an amount of " + debitRequest.getAmount() +
                    " has been successfully deposited into your account on " + formattedDateTime +"\n"+
                    "Your new balance after deposit: " + user.getAccountBalance() + "\n\n" +
                    "You can now use your balance for transactions with ease. \n"+
                    "If you have any questions, feel free to contact us.\n"+
                    "Thank you for choosing our services.\n\n" +
                    "Best regards,")
                .build();
        emailService.sendEmailAlert(details);


        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_CODE)
                .ResponseMessage(AccountUtils.ACCOUNT_CREDITED_message)
                .accountInfo(
                        AccountInfo.builder()
                                .accountName(user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
                                .accountNumber(debitRequest.getAccountNumber())
                                .accountBalance(user.getAccountBalance())
                                .build()
                )
                .build();
    }

    @Override
    @Transactional
    public BankResponse debitAccount(CreditDebitRequest request) {
        if (!userRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new UserException("Sorry, the account number does not exist.");
        }


        User user = userRepository.findByAccountNumber(request.getAccountNumber());

        BigDecimal requestAmount = request.getAmount();
        if (requestAmount == null || requestAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserException("Invalid transaction amount.");
        }

        BigDecimal currentBalance = user.getAccountBalance();
        if (requestAmount.compareTo(currentBalance) > 0) {
            throw new UserException("There is not enough balance to complete the transaction.");
        }

        BigDecimal dailyLimit = new BigDecimal("155000");
        BigDecimal totalWithdrawnToday = transactionRepository.getTotalWithdrawnToday(user.getAccountNumber());

        if (totalWithdrawnToday.add(requestAmount).compareTo(dailyLimit) > 0) {
            throw new UserException("Transaction exceeds daily withdrawal limit.");
        }

        user.setAccountBalance(currentBalance.subtract(requestAmount));


        Transaction transaction = new Transaction();
        transaction.setAccountNumber(user.getAccountNumber());
        transaction.setAmount(requestAmount);
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setUser(user);
        transactionRepository.save(transaction);


        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("Withdraw money")
                .messageBody("Dear " + user.getFirstName() +
                     "\nThis is to inform you that your account has been successfully debited.\n" +
                     "Transaction Details:\n" +
                     "Amount: " + request.getAmount() +
                     "\nDate: " + formattedDateTime +
                     "\nRemaining Balance: " + user.getAccountBalance()+
                     "\nIf you did not initiate this transaction or if you have any concerns, please contact our customer support immediately at" +
                     " email: " + senderEmail + "\nphone: 0775149653\n" +
                     "\nThank you for banking with us!")
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBITED)
                .ResponseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(user.getAccountBalance())
                        .accountNumber(user.getAccountNumber())
                        .accountName(user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
                        .build())
                .build();
    }

    @Override
    @Transactional
    public BankResponse transfer(Transfer transfer) {

        if (!userRepository.existsByAccountNumber(transfer.getSourceAccountNumber())) {
            throw new UserException("Sender's account number does not exist.");
        }

        if(!userRepository.existsByAccountNumber(transfer.getDestinationAccount())) {
            throw new UserException("The account number for the transfer is incorrect.");
        }

        BigDecimal amount = transfer.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserException("Invalid transaction amount.");
        }

        User source = userRepository.findByAccountNumber(transfer.getSourceAccountNumber()); // المرسل
        User destination = userRepository.findByAccountNumber(transfer.getDestinationAccount()); // المستلم

        BigDecimal currentAmountInSource = source.getAccountBalance();
        BigDecimal transferAmount = transfer.getAmount();
        BigDecimal destinationAmount = destination.getAccountBalance();

        if (transferAmount.compareTo(currentAmountInSource) > 0) {
            throw new UserException("There is not enough balance to complete the transaction.");
        }

        source.setAccountBalance(currentAmountInSource.subtract(transferAmount));

        Transaction sourceTransaction = new Transaction();
        sourceTransaction.setAccountNumber(source.getAccountNumber());
        sourceTransaction.setAmount(transferAmount);
        sourceTransaction.setTransactionType(TransactionType.DEBIT);
        sourceTransaction.setTransactionDate(LocalDate.now());
        sourceTransaction.setUser(source);
        transactionRepository.save(sourceTransaction);

        sendEmailNotification(source, destination, transferAmount, "Payment Transfer Confirmation", "sent");

        destination.setAccountBalance(destinationAmount.add(transferAmount));

        Transaction destinationTransaction = new Transaction();
        destinationTransaction.setAccountNumber(destination.getAccountNumber());
        destinationTransaction.setAmount(transferAmount);
        destinationTransaction.setTransactionType(TransactionType.CREDIT);
        destinationTransaction.setTransactionDate(LocalDate.now());
        destinationTransaction.setUser(destination);
        transactionRepository.save(destinationTransaction);

        sendEmailNotification(destination, source, transferAmount, "Payment Transfer Confirmation", "received");

        String messageInfo = "From " + source.getFirstName() +" to " + destination.getFirstName();

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESS)
                .ResponseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
                .accountInfo(
                        AccountInfo.builder()
                                .accountName(messageInfo)
                                .accountNumber("--")
                                .accountBalance(transferAmount)
                                .build()
                )
                .build();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new UserException("User Not Found"));
    }

    private void sendEmailNotification(User recipient, User sender, BigDecimal amount, String subject, String type) {
        String messageBody = "";
        if (type.equals("sent")) {
            messageBody = "Dear " + recipient.getFirstName() + ",\n\n" +
                    "We are pleased to inform you that your payment of $" + amount +
                    " has been successfully transferred to " + sender.getFirstName() + " " + sender.getLastName() +
                    ".\n\nThe transaction was completed on " + LocalDateTime.now() +
                    ".\nYour remaining balance is $" + recipient.getAccountBalance() + ".\n\n" +
                    "If you have any questions or need further assistance, please feel free to contact us.\n\n" +
                    "Thank you for using our service.";
        } else if (type.equals("received")) {
            messageBody = "Dear " + recipient.getFirstName() + ",\n\n" +
                    "You have received a payment of $" + amount +
                    " from " + sender.getFirstName() + " " + sender.getLastName() +
                    ".\n\nThe transaction was completed on " + LocalDateTime.now() +
                    ".\nYour new account balance is $" + recipient.getAccountBalance() + ".\n\n" +
                    "If you have any questions or need further assistance, please feel free to contact us.\n\n" +
                    "Thank you for using our service.";
        }

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(recipient.getEmail())
                .subject(subject)
                .messageBody(messageBody)
                .build();

        emailService.sendEmailAlert(emailDetails);
    }

}
