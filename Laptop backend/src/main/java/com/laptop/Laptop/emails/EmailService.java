package com.laptop.Laptop.emails;


import com.laptop.Laptop.dto.EmailDetails;

public interface EmailService {
        void sendEmail(EmailDetails emailDetails);
        void sendEmailAlertWithAttachment(EmailDetails emailDetails);
    }

