package com.laptop.Laptop.controller;

import com.laptop.Laptop.dto.EmailDetails;
import com.laptop.Laptop.services.SendEmails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private SendEmails sendEmails;

    // Endpoint to send email
    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailDetails emailDetails) {
        try {
            sendEmails.sendEmailToRecipient(emailDetails);
            return "Email sent successfully!";
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }
}