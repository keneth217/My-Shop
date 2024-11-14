package com.laptop.Laptop.services;

import com.laptop.Laptop.dto.EmailDetails;
import com.laptop.Laptop.emails.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendEmails {

    @Autowired
    private EmailService emailService;

    // Method to send email using EmailService
    //accept email sent to,subject and messege body
    public void sendEmailToRecipient(EmailDetails emailDetails) {
        emailService.sendEmail(emailDetails);
    }
}
