package com.laptop.Laptop.controller;

import com.laptop.Laptop.dto.EmailDetails;
import com.laptop.Laptop.services.SendEmails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            sendEmails.sendEmailToCustomer(emailDetails);
            return "Email sent successfully! to customer";
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }
    @PostMapping("/receive")
    public String receiveEmail(@RequestBody EmailDetails emailDetails) {
        try {
            sendEmails.receiveEmailFromCustomer(emailDetails);
            return "Email received successfully!  from customer";
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }

//    @PostMapping("/send/bulk")
//    public String sendBulkEmailToCustomers(@RequestBody EmailDetails emailDetails) {
//        try {
//            sendEmails.sendBulkEmailToCustomers(emailDetails);
//            return "Email sent successfully! to customer";
//        } catch (Exception e) {
//            return "Error sending email: " + e.getMessage();
//        }
//    }

    @PostMapping("/send/bulk")
    public ResponseEntity<String> sendBulkEmailToCustomers(@RequestBody EmailDetails emailDetails) {
        try {
            sendEmails.sendBulkEmailToCustomers(emailDetails);  // Process bulk email sending
            return ResponseEntity.ok("Emails sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending emails: " + e.getMessage());
        }
    }

}
