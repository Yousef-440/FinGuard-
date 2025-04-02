package com.example.JavaBank.service.impl;

import com.example.JavaBank.dto.EmailDetails;
import com.example.JavaBank.model.Transaction;
import com.example.JavaBank.model.User;
import com.example.JavaBank.repo.TransactionRepository;
import com.example.JavaBank.repo.UserRepository;
import com.example.JavaBank.service.EmailService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BankStatement {

    private static final Logger log = LoggerFactory.getLogger(BankStatement.class);
    @Autowired
    private TransactionRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private static final String FILE = "C:\\Users\\LENOVO\\Desktop\\Spring-test\\MyStatement.pdf";

    public List<Transaction> transactions(String accountNumber , String startDate , String endDate) throws FileNotFoundException, DocumentException{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");

        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        User user = userRepository.findByAccountNumber(accountNumber);
        String FullNameOfCustomer = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();

        List<Transaction> transactionList = repository.findAll().stream()
                .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction ->
                        !transaction.getTransactionDate().isBefore(start) &&
                                !transaction.getTransactionDate().isAfter(end)
                )
                .toList();




        Rectangle rect = new Rectangle(PageSize.A4);
        Document document = new Document(rect);

        log.info("setting size of document");
        OutputStream outputStream = new FileOutputStream(FILE);

        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable pdfPTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("Java Bank App", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Font.NORMAL, BaseColor.WHITE)));
        bankName.setBorder(0);
        bankName.setBackgroundColor(new BaseColor(0, 51, 102));
        bankName.setHorizontalAlignment(Element.ALIGN_CENTER);
        bankName.setPadding(15f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("Amman /Jordan", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, BaseColor.WHITE)));
        bankAddress.setBorder(0);
        bankAddress.setBackgroundColor(new BaseColor(0, 51, 102));
        bankAddress.setHorizontalAlignment(Element.ALIGN_CENTER);
        bankAddress.setPadding(10f);
        pdfPTable.addCell(bankName);
        pdfPTable.addCell(bankAddress);

        PdfPTable table = new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date: " + startDate, FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK)));
        customerInfo.setBorder(0);
        customerInfo.setPadding(10f);

        PdfPCell customerName = new PdfPCell(new Phrase("Name: " + FullNameOfCustomer, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.NORMAL, BaseColor.BLACK)));
        customerName.setBorder(0);
        customerName.setPadding(10f);

        PdfPCell address = new PdfPCell(new Phrase("Customer Address: " + user.getAddress(), FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK)));
        address.setBorder(0);
        address.setPadding(10f);

        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.NORMAL, BaseColor.BLACK)));
        statement.setBorder(0);
        statement.setColspan(2);
        statement.setHorizontalAlignment(Element.ALIGN_CENTER);
        statement.setPadding(15f);

        PdfPCell DateEnd = new PdfPCell(new Phrase("End Date: " + endDate, FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK)));
        DateEnd.setBorder(0);
        DateEnd.setPadding(10f);

        PdfPCell space = new PdfPCell();
        space.setBorder(0);

        table.addCell(customerInfo);
        table.addCell(statement);
        table.addCell(DateEnd);
        table.addCell(customerName);
        table.addCell(space);
        table.addCell(address);


        PdfPTable transactionTable = new PdfPTable(3);
        PdfPCell date = new PdfPCell(new Phrase("DATE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.NORMAL, BaseColor.WHITE)));
        date.setBackgroundColor(new BaseColor(0, 51, 102));
        date.setHorizontalAlignment(Element.ALIGN_CENTER);
        date.setPadding(10f);

        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.NORMAL, BaseColor.WHITE)));
        transactionType.setBackgroundColor(new BaseColor(0, 51, 102));
        transactionType.setHorizontalAlignment(Element.ALIGN_CENTER);
        transactionType.setPadding(10f);

        PdfPCell Amount = new PdfPCell(new Phrase("TRANSACTION Amount", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.NORMAL, BaseColor.WHITE)));
        Amount.setBackgroundColor(new BaseColor(0, 51, 102));
        Amount.setHorizontalAlignment(Element.ALIGN_CENTER);
        Amount.setPadding(10f);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(Amount);

        transactionList.forEach(transaction -> {
            PdfPCell transactionDateCell = new PdfPCell(new Phrase(transaction.getTransactionDate().toString(), FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK)));
            transactionDateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            transactionDateCell.setPadding(8f);

            PdfPCell transactionTypeCell = new PdfPCell(new Phrase(transaction.getTransactionType().toString(), FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK)));
            transactionTypeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            transactionTypeCell.setPadding(8f);

            PdfPCell transactionAmountCell = new PdfPCell(new Phrase(transaction.getAmount().toString(), FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK)));
            transactionAmountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionAmountCell.setPadding(8f);

            transactionTable.addCell(transactionDateCell);
            transactionTable.addCell(transactionTypeCell);
            transactionTable.addCell(transactionAmountCell);
        });

        document.add(pdfPTable);
        document.add(table);
        document.add(transactionTable);

        document.close();

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Dear " + user.getFirstName() + "\n\n" +
                        "I hope you're doing well.\n" +
                        "Please find attached your detailed transaction statement, which includes all withdrawals, deposits, and " +
                        "transfers made during the period.\n\n" +
                        "You can find the attached file below.\n\n" +
                        "If you have any questions or need further clarification, feel free to reach out.\n\n" +
                        "Best regards,\n\n" +
                        "Yousef Jaber.")
                .attachment(FILE)
                .build();

        emailService.sendEmailWithAttachment(emailDetails);

        return transactionList;
    }


}
