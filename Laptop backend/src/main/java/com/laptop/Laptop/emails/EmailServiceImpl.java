package com.laptop.Laptop.emails;

import com.laptop.Laptop.dto.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;


    @Override
    public void sendEmailToCustomer(EmailDetails emailDetails) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(emailDetails.getRecipient());
            mailMessage.setText(emailDetails.getMessageBody());
            mailMessage.setSubject(emailDetails.getSubject());

            javaMailSender.send(mailMessage);
            System.out.println("Mail sent successfully");
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void receiveEmailFromCustomer(EmailDetails emailDetails) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            // Set the 'from' and 'to' as the senderEmail to send the email to itself
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(senderEmail);  // Sending to the sender's email
            mailMessage.setText(emailDetails.getMessageBody());
            mailMessage.setSubject(emailDetails.getSubject());

            javaMailSender.send(mailMessage);
            System.out.println("Mail sent successfully to sender email");
        } catch (MailException e) {
            throw new RuntimeException("Error sending email to sender", e);
        }
    }
    @Override
    public void sendEmailAlertWithAttachment(EmailDetails emailDetails) {
        File pdfFile = new File("src/main/resources/shop.pdf");

        if (!pdfFile.exists()) {
            throw new RuntimeException("Attachment file does not exist: " + pdfFile.getAbsolutePath());
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(senderEmail);
            mimeMessageHelper.setTo(emailDetails.getRecipient());
            mimeMessageHelper.setText(emailDetails.getMessageBody());
            mimeMessageHelper.setSubject(emailDetails.getSubject());

            // Add the attachment
            FileSystemResource file = new FileSystemResource(pdfFile);
            mimeMessageHelper.addAttachment(file.getFilename(), file);

            // Check if the file exists before adding it as an attachment
            if (file.exists()) {
                mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
                javaMailSender.send(mimeMessage);
                System.out.println(file.getFilename() + " Shop management PDF sent successfully to " + emailDetails.getRecipient());
            } else {
                System.out.println("Attachment file does not exist: " + file);
            }

        } catch (MessagingException e) {
            // Log or handle the exception
            e.printStackTrace();
            throw new RuntimeException("Error while sending email with attachment", e);
        }
    }


}