package com.laptop.Laptop.emails;


import com.laptop.Laptop.dto.EmailDetails;

public interface EmailService {
        void sendEmailToCustomer(EmailDetails emailDetails);
        void sendEmailAlertWithAttachment(EmailDetails emailDetails);

    void sendBulkEmailToCustomers(EmailDetails emailDetails);
    void receiveEmailFromCustomer(EmailDetails emailDetails);
    }

