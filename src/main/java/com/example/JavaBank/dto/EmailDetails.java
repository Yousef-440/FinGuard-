package com.example.JavaBank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetails {
    private String recipient;//البريد الإلكتروني المدخل من قبل المستخدم
    private String messageBody;//محتوى الرسالة
    private String subject;//موضوع الرسالة
    private String attachment;

}
