package org.example.services;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;

        long maxHeapSize = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        Dotenv dotenv = Dotenv.load();

        // Get the MAIL_PASSWORD from the environment variables
        String mailPassword = dotenv.get("MAIL_PASSWORD");
        JavaMailSenderImpl javaMailSenderImpl = (JavaMailSenderImpl) mailSender;
        javaMailSenderImpl.setPassword(mailPassword);
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setFrom("lucentini.nicolas@gmail.com"); // Sender email
            helper.setSubject(subject);
            helper.setText(body, true); // 'true' allows HTML content

            mailSender.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
    public void sendEmailWithAttachment(String to, String subject, String body, ByteArrayResource file, String fileName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setFrom("lucentini.nicolas@gmail.com"); // Sender email
            helper.setSubject(subject);
            helper.setText(body, true); // 'true' allows HTML content
            helper.addAttachment(fileName, file);

            mailSender.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}
